package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.PurchaseDistributionDto;
import br.com.inovasoft.epedidos.models.entities.PurchaseDistribution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi", uses = { PurchaseItemMapper.class })
public interface PurchaseDistributionMapper extends BaseMapper<PurchaseDistribution, PurchaseDistributionDto> {

    @Override
    @Mapping(source = "purchaseItem.product.id", target = "idProduct")
    @Mapping(source = "purchaseItem.product.name", target = "nameProduct")
    @Mapping(source = "purchaseItem.unitValue", target = "unitValue")
    @Mapping(source = "purchaseItem.purchase.id", target = "idPurchase")
    @Mapping(source = "orderItem.order.id", target = "idOrder")
    PurchaseDistributionDto toDto(PurchaseDistribution entity);
}