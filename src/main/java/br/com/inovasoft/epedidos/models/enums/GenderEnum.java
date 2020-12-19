package br.com.inovasoft.epedidos.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderEnum {
    
    M("Male"), F("Female");

    private final String description;

}