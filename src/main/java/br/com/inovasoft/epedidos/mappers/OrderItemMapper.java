package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.inovasoft.epedidos.models.dtos.OrderItemDto;
import br.com.inovasoft.epedidos.models.entities.OrderItem;

@Mapper(componentModel = "cdi")
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemDto> {
    @Override
    @Mappings({ @Mapping(target = "product.id", source = "idProduct") })
    OrderItem toEntity(OrderItemDto dto);

    @Override
    @Mappings({ @Mapping(target = "idProduct", source = "product.id"),
            @Mapping(target = "nameProduct", source = "product.name") })
    OrderItemDto toDto(OrderItem entity);
}