package br.com.inovasoft.epedidos.models.enums;

import lombok.Getter;

public enum PackageTypeEnum {

    RETURNABLE("RETURNABLE"), DISPOSABLE("DISPOSABLE");

    @Getter
    private final String description;

    PackageTypeEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}