package br.com.inovasoft.epedidos.models.enums;

import lombok.Getter;

public enum OrderEnum {

    OPEN("ABERTO"),
    PURCHASE("COMPRA"),
    DISTRIBUTED("DISTRIBUIDO"),
    FINISHED("FECHADO"),
    BILLED("FATURADO"),
    CANCELED("CANCELADO");

    @Getter
    private final String description;

    OrderEnum(String description) {
        this.description = description;
    }

}