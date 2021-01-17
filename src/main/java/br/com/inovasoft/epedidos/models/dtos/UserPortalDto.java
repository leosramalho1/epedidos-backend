package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class UserPortalDto {

    private Long id;
    private String name;

}