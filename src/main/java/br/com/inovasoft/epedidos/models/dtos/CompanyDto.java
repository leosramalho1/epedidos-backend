package br.com.inovasoft.epedidos.models.dtos;

import java.util.List;

import lombok.Data;

@Data
public class CompanyDto {

    private Long id;
    private String name;
    private String business_name;
    private String cnpj;
    private String email;
    private String telefone;

    List<CompanySystemDto> systems;

}