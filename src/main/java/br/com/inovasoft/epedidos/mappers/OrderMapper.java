package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.entities.Order;

@Mapper(componentModel = "cdi", uses = { OrderItemMapper.class })
public interface OrderMapper extends BaseMapper<Order, OrderDto> {

    @Override
    @Mappings({ @Mapping(target = "customer.id", source = "idCustomer") })
    Order toEntity(OrderDto dto);

    @Override
    @Mappings({ @Mapping(target = "idCustomer", source = "customer.id"),
            @Mapping(target = "nameCustomer", source = "customer.name") })
    OrderDto toDto(Order entity);

}