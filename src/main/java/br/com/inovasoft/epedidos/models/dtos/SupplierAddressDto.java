package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

@Data
public class SupplierAddressDto {

    private Long id;
    private AddressDto address;
    private SupplierDto supplier;
    private boolean primaryAddress;
    private boolean deliveryAddress;

}