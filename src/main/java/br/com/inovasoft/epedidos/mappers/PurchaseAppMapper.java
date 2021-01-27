package br.com.inovasoft.epedidos.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.inovasoft.epedidos.models.dtos.PurchaseAppDto;
import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;

@Mapper(componentModel = "cdi", uses = {PurchaseItemMapper.class })
public interface PurchaseAppMapper{

    @Mappings({ @Mapping(target = "idSupplier", source = "supplier.id"),
            @Mapping(target = "supplierName", source = "supplier.name"),
            @Mapping(target = "idBuyer", source = "buyer.id"),
            @Mapping(target = "buyerName", source = "buyer.name") })
    PurchaseAppDto to(PurchaseDto dto);


    List<PurchaseAppDto> to(List<PurchaseDto> entity);

    @Mappings({ @Mapping(target = "supplier.id", source = "idSupplier"),
            @Mapping(target = "supplier.name", source = "supplierName"),
            @Mapping(target = "buyer.id", source = "idBuyer"),
            @Mapping(target = "buyer.name", source = "buyerName") })
    PurchaseDto from(PurchaseAppDto dto);
}