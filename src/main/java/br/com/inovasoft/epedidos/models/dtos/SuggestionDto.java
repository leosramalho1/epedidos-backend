package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

@Data
public class SuggestionDto {

    private Long id;
    private String name;
    private String codeName;

    public SuggestionDto(Long id, String name) {
        this.id = id;
        this.name = name;
        this.codeName = this.id + " - " + this.name;
    }

}