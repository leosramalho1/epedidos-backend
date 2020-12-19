package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum PackageTypeEnum {

    RETURNABLE("RETURNABLE"), DISPOSABLE("DISPOSABLE");

    @Getter(onMethod = @__(@JsonValue))
    private final String description;

    PackageTypeEnum(String description) {
        this.description = description;
    }

    @JsonCreator
    public static PackageTypeEnum fromValue(String value) {
        return Stream.of(PackageTypeEnum.values()).filter(e -> e.getDescription().equals(value)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return description;
    }
}