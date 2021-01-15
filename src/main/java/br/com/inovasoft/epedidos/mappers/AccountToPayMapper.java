package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.AccountToPayDto;
import br.com.inovasoft.epedidos.models.entities.AccountToPay;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { SupplierMapper.class })
public interface AccountToPayMapper extends BaseMapper<AccountToPay, AccountToPayDto> {

//        @Override
//        @Mappings({ @Mapping(target = "supplier.id", source = "idSupplier") })
//        AccountToPay toEntity(AccountToPayDto dto);
//
//        @Override
//        @Mappings({ @Mapping(target = "idSupplier", source = "supplier.id"),
//                        @Mapping(target = "nameSupplier", source = "supplier.name") })
//        AccountToPayDto toDto(AccountToPay entity);

}