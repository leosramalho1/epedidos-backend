package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.models.entities.Purchase;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "cdi", uses = { SupplierMapper.class, PurchaseItemMapper.class, UserPortalMapper.class })
public interface PurchaseMapper extends BaseMapper<Purchase, PurchaseDto> {

    @Mappings({                                   
        @Mapping(target = "dueDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    })
    Purchase toEntity(PurchaseDto dto);
}