package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.AddressDto;
import br.com.inovasoft.epedidos.models.entities.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface AddressMapper extends BaseMapper<Address, AddressDto> {

}