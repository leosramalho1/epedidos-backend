package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.SupplierAddressDto;
import br.com.inovasoft.epedidos.models.entities.SupplierAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { AddressMapper.class, SupplierMapper.class })
public interface SupplierAddressMapper extends BaseMapper<SupplierAddress, SupplierAddressDto> {

}