package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

@Data
public class CustomerAddressDto {

    private Long id;
    private AddressDto address;
    private boolean primaryAddress;
    private boolean deliveryAddress;

}