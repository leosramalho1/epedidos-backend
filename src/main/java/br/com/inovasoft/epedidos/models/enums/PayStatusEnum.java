package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

public enum PayStatusEnum {

    PAID("PAGO"), OVERDUE("VENCIDO"), CANCELED("CANCELADO"),
    OPEN("AGUARDANDANDO_PAGAMENTO"), PARTIALLY_PAID("PARCIALMENTE_PAGO"),
    PAID_OVERDUE("PAGO_EM_ATRASO");

    @Getter
    private final String description;

    PayStatusEnum(String description) {
        this.description = description;
    }

    @JsonCreator
    public static PayStatusEnum fromValue(String value) {
        return Stream.of(PayStatusEnum.values())
                .filter(e -> e.getDescription().equals(value))
                .findFirst().orElse(null);
    }


}