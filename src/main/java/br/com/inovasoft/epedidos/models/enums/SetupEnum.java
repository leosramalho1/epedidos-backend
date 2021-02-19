package br.com.inovasoft.epedidos.models.enums;

import lombok.Getter;

public enum SetupEnum {

    CRON_ORDER_TO_PURCHASE("CRON_ORDER_TO_PURCHASE");

    @Getter
    private final String description;

    SetupEnum(String description) {
        this.description = description;
    }

}