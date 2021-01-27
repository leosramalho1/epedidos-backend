package br.com.inovasoft.epedidos.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AccountEnum {
    OPEN("ABERTO"), PAY("PAGO"), CANCEL("CANCELADO");

    @Getter
    private final String description;

}