package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CustomerUserDto {

    private Long id;
    private String name;
    private String cpfCnpj;
    private String phone;
    private String password;
    private String email;
}