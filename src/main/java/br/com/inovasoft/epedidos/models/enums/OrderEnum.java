package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum OrderEnum {
    OPEN("ABERTA"), CANCEL("CANCELADA"), FINISHED("FINALIZADA");

    @Getter(onMethod = @__(@JsonValue))
    private final String description;

    OrderEnum(String description) {
        this.description = description;
    }

    @JsonCreator
    public static OrderEnum fromValue(String value) {
        return Stream.of(OrderEnum.values()).filter(e -> e.getDescription().equals(value)).findFirst().orElse(null);
    }
}