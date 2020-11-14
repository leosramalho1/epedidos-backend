package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum RoleEnum {
    ADMIN("ADMIN"), BUYER("BUYER");

    @Getter(onMethod = @__(@JsonValue))
    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

    @JsonCreator
    public static RoleEnum fromValue(String value) {
        return Stream.of(RoleEnum.values()).filter(e -> e.getDescription().equals(value)).findFirst().orElse(null);
    }
}