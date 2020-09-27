package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

@Data
public class CompanySystemDto {

    private Long id;
    private Long idSystem;
    private String nameSystem;
    private String systemKey;
    private String status;
    private String emailAdmin;
    private Integer maxUser;

}