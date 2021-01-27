package br.com.inovasoft.epedidos.models.enums;

import lombok.Getter;

public enum RoleEnum {
    ADMIN("ADMIN"), BUYER("BUYER");

    @Getter
    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

}