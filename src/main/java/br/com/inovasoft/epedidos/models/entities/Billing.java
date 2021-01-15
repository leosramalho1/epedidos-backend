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

    BigDecimal getPaidOut();

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
        return getPaidOut().compareTo(totalValue()) >= 0 && getPaidOutDate() != null;
    }

    default boolean isPartiallyPaid() {
        return getPaidOut().compareTo(totalValue()) < 0 && !isOverdue();
    }

    default boolean isAwaitingPaid() {
        return getPaidOut().compareTo(BigDecimal.ZERO) == 0 && !isOverdue();
    }

    default boolean isOverdue() {
        return LocalDate.now().isAfter(getDueDate()) && !isPaid();
    }

    default boolean isCanceled() {
        return getDeletedOn() != null;
    }

    default PayStatusEnum getStatus() {

        if(isCanceled()) {
            return PayStatusEnum.CANCELED;
        }

        if(isPaid()) {
            return PayStatusEnum.PAID;
        }

        if(isOverdue()) {
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
