package br.com.inovasoft.epedidos.models.enums;

import lombok.Getter;

public enum OrderEnum {
    OPEN("ABERTA"), CANCEL("CANCELADA"), FINISHED("FINALIZADA");

    @Getter
    private final String description;

    OrderEnum(String description) {
        this.description = description;
    }

}