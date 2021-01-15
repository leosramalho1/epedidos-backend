package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.models.entities.Purchase;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { SupplierMapper.class, PurchaseItemMapper.class })
public interface PurchaseMapper extends BaseMapper<Purchase, PurchaseDto> {

//    @Override
//    @Mappings({ @Mapping(target = "supplier.id", source = "idSupplier"),
//            @Mapping(target = "buyer.id", source = "idBuyer") })
//    Purchase toEntity(PurchaseDto dto);
//
//    @Override
//    @Mappings({ @Mapping(target = "idSupplier", source = "supplier.id"),
//            @Mapping(target = "nameSupplier", source = "supplier.name"),
//            @Mapping(target = "idBuyer", source = "buyer.id"), @Mapping(target = "nameBuyer", source = "buyer.name") })
//    PurchaseDto toDto(Purchase entity);

}