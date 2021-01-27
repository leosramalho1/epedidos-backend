package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface Billing {

    BigDecimal getOriginalValue();

    BigDecimal getTaxValue();

    LocalDate getDueDate();

    BigDecimal getPaidOutValue();

    LocalDate getPaidOutDate();

    LocalDateTime getDeletedOn();

    default BigDecimal totalValue() {

        BigDecimal originalValue = Optional.ofNullable(getOriginalValue())
                .orElse(BigDecimal.ZERO);
        BigDecimal taxValue = Optional.ofNullable(getTaxValue())
                .orElse(BigDecimal.ZERO);

        return originalValue.add(taxValue) ;
    }

    default boolean isPaid() {
        return Optional.ofNullable(getPaidOutValue()).orElse(BigDecimal.ZERO)
                .compareTo(totalValue()) >= 0 && getPaidOutDate() != null;
    }

    default boolean isPartiallyPaid() {
        return Optional.ofNullable(getPaidOutValue()).orElse(BigDecimal.ZERO)
                .compareTo(totalValue()) < 0 && !isOverdueNotPaid();
    }

    default boolean isAwaitingPaid() {
        return Optional.ofNullable(getPaidOutValue()).orElse(BigDecimal.ZERO)
                .compareTo(BigDecimal.ZERO) == 0 && !isOverdueNotPaid();
    }

    default boolean isOverdueNotPaid() {
        return isOverdue() && !isPaid();
    }

    default boolean isOverdue() {
        return getDueDate() != null && Optional.ofNullable(getPaidOutDate())
                .orElse(LocalDate.now()).isAfter(getDueDate());
    }

    default boolean isCanceled() {
        return getDeletedOn() != null;
    }

    default PayStatusEnum getStatus() {

        if(isCanceled()) {
            return PayStatusEnum.CANCELED;
        }

        if(isOverdue() && isPaid()) {
            return PayStatusEnum.PAID_OVERDUE;
        }

        if(isPaid()) {
            return PayStatusEnum.PAID;
        }

        if(isOverdueNotPaid()) {
            return PayStatusEnum.OVERDUE;
        }

        if(isAwaitingPaid()) {
            return PayStatusEnum.OPEN;
        }

        if(isPartiallyPaid()) {
            return PayStatusEnum.PARTIALLY_PAID;
        }


        return PayStatusEnum.OPEN;
    }
}
