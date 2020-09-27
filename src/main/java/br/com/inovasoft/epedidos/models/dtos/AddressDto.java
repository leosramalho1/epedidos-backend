package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

@Data
public class AddressDto {

    private Long id;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private Long idCity;
    private String cityName;
    private String stateName;
    private String zipCode;

}