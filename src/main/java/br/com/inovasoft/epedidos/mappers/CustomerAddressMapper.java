package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.CustomerAddressDto;
import br.com.inovasoft.epedidos.models.entities.CustomerAddress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { AddressMapper.class, CustomerMapper.class })
public interface CustomerAddressMapper extends BaseMapper<CustomerAddress, CustomerAddressDto> {

}