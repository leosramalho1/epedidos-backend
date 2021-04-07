package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.AccountToPayDto;
import br.com.inovasoft.epedidos.models.entities.AccountToPay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = { SupplierMapper.class, PurchaseMapper.class, PurchaseItemMapper.class, PaymentMethodMapper.class })
public interface AccountToPayMapper extends BaseMapper<AccountToPay, AccountToPayDto> {

    @Override
    @Mapping(source = "purchase.id", target = "purchaseId")
    AccountToPayDto toDto(AccountToPay entity);

}