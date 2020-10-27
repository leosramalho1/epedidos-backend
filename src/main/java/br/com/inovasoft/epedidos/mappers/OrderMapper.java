package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;

import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.entities.Order;

@Mapper(componentModel = "cdi", uses = { OrderItemMapper.class })
public interface OrderMapper extends BaseMapper<Order, OrderDto> {

}