package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CashFlowDto {

    private LocalDate cashFlowDate;
    private BigDecimal balance;
    private BigDecimal receivedValue;
    private BigDecimal paidValue;
    @Builder.Default
    private List<AccountToReceiveDto> accountsToReceive = new ArrayList<>();
    @Builder.Default
    private List<AccountToPayDto> accountsToPay = new ArrayList<>();

    public BigDecimal getBalance() {
        return getReceivedValue().subtract(getPaidValue());
    }

    public BigDecimal getReceivedValue() {
        return getAccountsToReceive().stream()
                .map(AccountToReceiveDto::getPaidOutValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPaidValue() {
        return getAccountsToPay().stream()
                .map(AccountToPayDto::getPaidOutValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<AccountToReceiveDto> getAccountsToReceive() {
        return Optional.ofNullable(accountsToReceive).orElse(Collections.emptyList());
    }

    public List<AccountToPayDto> getAccountsToPay() {
        return Optional.ofNullable(accountsToPay).orElse(Collections.emptyList());
    }
}