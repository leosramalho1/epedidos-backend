package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.util.SuggestionUtil;
import lombok.Data;

@Data
public class SuggestionDto {

    private String codeName;

    public SuggestionDto(Long id, String name) {
        this.codeName = SuggestionUtil.build(id, name);
    }

}