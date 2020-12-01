package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.entities.AccountToReceive;

@Mapper(componentModel = "cdi")
public interface AccountToReceiveMapper extends BaseMapper<AccountToReceive, AccountToReceiveDto> {

        @Override
        @Mappings({ @Mapping(target = "customer.id", source = "idCustomer") })
        AccountToReceive toEntity(AccountToReceiveDto dto);

        @Override
        @Mappings({ @Mapping(target = "idCustomer", source = "customer.id"),
                        @Mapping(target = "nameCustomer", source = "customer.name") })
        AccountToReceiveDto toDto(AccountToReceive entity);

}