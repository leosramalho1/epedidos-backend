package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.inovasoft.epedidos.models.dtos.AddressDto;
import br.com.inovasoft.epedidos.models.entities.Address;

@Mapper(componentModel = "cdi")

public interface AddressMapper extends BaseMapper<Address, AddressDto> {
    @Override
    @Mappings({ @Mapping(target = "city.id", source = "idCity") })
    Address toEntity(AddressDto dto);

    @Override
    @Mappings({ @Mapping(target = "idCity", source = "city.id"), @Mapping(target = "cityName", source = "city.name"),
            @Mapping(target = "stateName", source = "city.uf") })
    AddressDto toDto(Address entity);
}