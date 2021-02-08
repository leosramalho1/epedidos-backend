package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.entities.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = { OrderItemMapper.class, CustomerMapper.class })
public interface OrderMapper extends BaseMapper<Order, OrderDto> {

    @Override
    @Mapping(target = "customer.id", source = "idCustomer")
    Order toEntity(OrderDto dto);

    @Override
    @Mapping(target = "idCustomer", source = "customer.id")
    @Mapping(target = "nameCustomer", source = "customer.name")
    OrderDto toDto(Order entity);


}