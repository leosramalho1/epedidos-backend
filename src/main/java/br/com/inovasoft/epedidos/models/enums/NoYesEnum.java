package br.com.inovasoft.epedidos.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum NoYesEnum {
    @JsonProperty("Não")
    NO("Não"),
    @JsonProperty("Sim")
    YES("Sim");

    @Getter(onMethod = @__(@JsonValue))
    private final String description;

    @JsonCreator
    public static NoYesEnum fromValue(String value) {
       return Stream.of(NoYesEnum.values())
               .filter(e -> e.getDescription().equals(value))
               .findFirst().orElse(null);
    }

}