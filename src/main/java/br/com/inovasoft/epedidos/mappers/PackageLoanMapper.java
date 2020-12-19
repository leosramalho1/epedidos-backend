package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.PackageLoanDto;
import br.com.inovasoft.epedidos.models.entities.PackageLoan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = {SupplierMapper.class, CustomerMapper.class, OrderItemMapper.class})
public interface PackageLoanMapper extends BaseMapper<PackageLoan, PackageLoanDto> {

}