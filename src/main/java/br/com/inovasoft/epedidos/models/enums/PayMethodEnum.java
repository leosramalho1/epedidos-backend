package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum PayMethodEnum {

    MONEY("DINHEIRO"),
    CHEQUE("CHEQUE"),
    CREDIT_CARD("CARTAO CREDITO"),
    DEBIT_CARD("CARTAO DEBITO");

    @Getter(onMethod_ = @JsonValue)
    private final String description;

    PayMethodEnum(String description) {
        this.description = description;
    }

    @JsonCreator
    public static PayMethodEnum fromValue(String value) {
        return Stream.of(PayMethodEnum.values())
                .filter(e -> e.getDescription().equals(value))
                .findFirst().orElse(null);
    }


}