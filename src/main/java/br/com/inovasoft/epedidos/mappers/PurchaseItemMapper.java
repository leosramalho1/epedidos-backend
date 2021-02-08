package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.PurchaseItemDto;
import br.com.inovasoft.epedidos.models.entities.PurchaseItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface PurchaseItemMapper extends BaseMapper<PurchaseItem, PurchaseItemDto> {

    @Override
    @Mapping(target = "product.id", source = "idProduct")
    PurchaseItem toEntity(PurchaseItemDto dto);

    @Override
    @Mapping(target = "idProduct", source = "product.id")
    @Mapping(target = "nameProduct", source = "product.name")
    @Mapping(target = "averageValue", expression = "java(entity.calculateAverageValue())")
    PurchaseItemDto toDto(PurchaseItem entity);

}