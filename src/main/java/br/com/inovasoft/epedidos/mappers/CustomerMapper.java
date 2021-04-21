package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.CustomerBillingDto;
import br.com.inovasoft.epedidos.models.dtos.CustomerDto;
import br.com.inovasoft.epedidos.models.entities.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { CustomerUserMapper.class, CustomerAddressMapper.class, CustomerUserMapper.class })
public interface CustomerMapper extends BaseMapper<Customer, CustomerDto> {

    CustomerBillingDto toBillingClosingDto(Customer entity);

}