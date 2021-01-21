package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.models.entities.Purchase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { SupplierMapper.class, PurchaseItemMapper.class, UserPortalMapper.class })
public interface PurchaseMapper extends BaseMapper<Purchase, PurchaseDto> {


}