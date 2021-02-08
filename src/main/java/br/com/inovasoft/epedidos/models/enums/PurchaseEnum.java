package br.com.inovasoft.epedidos.models.enums;

import lombok.Getter;

public enum PurchaseEnum {

    OPEN("ABERTO"),
    DISTRIBUTED("DISTRIBUIDO"),
    FINISHED("FINALIZADO"),
    CANCELED("CANCELADO");

    @Getter
    private final String description;

    PurchaseEnum(String description) {
        this.description = description;
    }

}