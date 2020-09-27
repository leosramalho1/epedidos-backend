package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.SupplierDto;
import br.com.inovasoft.epedidos.models.entities.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { AddressMapper.class })
public interface SupplierMapper extends BaseMapper<Supplier, SupplierDto> {

}