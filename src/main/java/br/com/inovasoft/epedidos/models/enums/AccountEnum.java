package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum AccountEnum {
    OPEN("ABERTO"), PAY("PAGO"), CANCEL("CANCELADO");

    @Getter(onMethod = @__(@JsonValue))
    private final String description;

    @JsonCreator
    public static AccountEnum fromValue(String value) {
        return Stream.of(AccountEnum.values()).filter(e -> e.getDescription().equals(value)).findFirst().orElse(null);
    }
}