package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

@Data
public class OptionDto {

    private String value;
    private String text;

    public OptionDto(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public OptionDto(Long value, String text) {
        this.value = value.toString();
        this.text = text;
    }

}