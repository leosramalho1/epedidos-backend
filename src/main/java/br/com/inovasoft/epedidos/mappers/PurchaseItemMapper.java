package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.inovasoft.epedidos.models.dtos.PurchaseItemDto;
import br.com.inovasoft.epedidos.models.entities.PurchaseItem;

@Mapper(componentModel = "cdi")
public interface PurchaseItemMapper extends BaseMapper<PurchaseItem, PurchaseItemDto> {
    @Override
    @Mappings({ @Mapping(target = "product.id", source = "idProduct") })
    PurchaseItem toEntity(PurchaseItemDto dto);

    @Override
    @Mappings({ @Mapping(target = "idProduct", source = "product.id"),
            @Mapping(target = "nameProduct", source = "product.name") })
    PurchaseItemDto toDto(PurchaseItem entity);
}