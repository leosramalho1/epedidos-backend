package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

@Data
public class ChangePassDto {
    
    private String password;
    private String confirmPassword;

}