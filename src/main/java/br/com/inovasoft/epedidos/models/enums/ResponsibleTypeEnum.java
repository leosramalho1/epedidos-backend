package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum ResponsibleTypeEnum {

    Cliente("Cliente"), Fornecedor("Fornecedor");

    @Getter(onMethod = @__(@JsonValue))
    private final String description;

    ResponsibleTypeEnum(String description) {
        this.description = description;
    }

    @JsonCreator
    public static ResponsibleTypeEnum fromValue(String value) {
        return Stream.of(ResponsibleTypeEnum.values()).filter(e -> e.getDescription().equals(value)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return description;
    }
}