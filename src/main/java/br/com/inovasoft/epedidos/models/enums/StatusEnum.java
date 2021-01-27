package br.com.inovasoft.epedidos.models.enums;

import lombok.Getter;

public enum StatusEnum {

    INACTIVE("INATIVO"), ACTIVE("ATIVO");

    @Getter
    private final String description;

    StatusEnum(String description) {
        this.description = description;
    }

}