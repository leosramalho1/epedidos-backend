package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum StatusEnum {
    INACTIVE("INATIVO"), ACTIVE("ATIVO");

    @Getter(onMethod = @__(@JsonValue))
    private final String description;

    StatusEnum(String description) {
        this.description = description;
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
        return Stream.of(StatusEnum.values())
                .filter(e -> e.getDescription().equals(value))
                .findFirst().orElse(null);
    }
}