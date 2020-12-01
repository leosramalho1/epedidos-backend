package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.models.entities.Purchase;

@Mapper(componentModel = "cdi", uses = { PurchaseItemMapper.class })
public interface PurchaseMapper extends BaseMapper<Purchase, PurchaseDto> {

    @Override
    @Mappings({ @Mapping(target = "supplier.id", source = "idSupplier"),
            @Mapping(target = "buyer.id", source = "idBuyer") })
    Purchase toEntity(PurchaseDto dto);

    @Override
    @Mappings({ @Mapping(target = "idSupplier", source = "supplier.id"),
            @Mapping(target = "nameSupplier", source = "supplier.name"),
            @Mapping(target = "idBuyer", source = "buyer.id"), @Mapping(target = "nameBuyer", source = "buyer.name") })
    PurchaseDto toDto(Purchase entity);

}