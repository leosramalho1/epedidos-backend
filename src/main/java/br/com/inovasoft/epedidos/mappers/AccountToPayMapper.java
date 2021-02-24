package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.AccountToPayDto;
import br.com.inovasoft.epedidos.models.entities.AccountToPay;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { SupplierMapper.class, PurchaseMapper.class, PurchaseItemMapper.class })
public interface AccountToPayMapper extends BaseMapper<AccountToPay, AccountToPayDto> {


}