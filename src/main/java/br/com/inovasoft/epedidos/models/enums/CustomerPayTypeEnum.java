package br.com.inovasoft.epedidos.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CustomerPayTypeEnum {
    V("VOLUME"), P("PORCENTAGEM");

    @Getter
    private final String description;

    public String toString() {
        return getDescription();
    }
}