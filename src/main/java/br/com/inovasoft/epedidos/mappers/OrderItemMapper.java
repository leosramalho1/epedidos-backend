package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;

import br.com.inovasoft.epedidos.models.dtos.OrderItemDto;
import br.com.inovasoft.epedidos.models.entities.OrderItem;

@Mapper(componentModel = "cdi")
public interface OrderItemMapper extends BaseMapper<OrderItem, OrderItemDto> {

}