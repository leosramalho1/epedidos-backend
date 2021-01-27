package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.entities.Order;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "cdi", imports = { PayStatusEnum.class }, uses = { OrderItemMapper.class, CustomerMapper.class })
public interface OrderMapper extends BaseMapper<Order, OrderDto> {

    @Override
    @Mappings({ @Mapping(target = "customer.id", source = "idCustomer") })
    Order toEntity(OrderDto dto);

    @Override
    @Mappings({ @Mapping(target = "idCustomer", source = "customer.id"),
            @Mapping(target = "nameCustomer", source = "customer.name") })
    OrderDto toDto(Order entity);

    @Mapping(target = "originalValue", source = "totalValueProductsRealized")
    @Mapping(target = "paidOutValue", constant = "0.00")
    @Mapping(target = "status", expression = "java(PayStatusEnum.OPEN)")
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "id", ignore = true)
    AccountToReceiveDto toAccountToReceiveDto(Order entity);

    List<AccountToReceiveDto> toAccountToReceiveDto(List<Order> entity);

}