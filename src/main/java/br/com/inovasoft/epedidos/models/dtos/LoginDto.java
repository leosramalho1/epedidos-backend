package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

@Data
public class LoginDto {

    private String cpfCnpj;
    private String email;
    private String userName;
    private String password;
    private String rememberme;
    private String token;

}