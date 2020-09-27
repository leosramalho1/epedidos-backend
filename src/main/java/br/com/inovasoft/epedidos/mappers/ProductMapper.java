package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.ProductDto;
import br.com.inovasoft.epedidos.models.entities.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { CategoryMapper.class })
public interface ProductMapper extends BaseMapper<Product, ProductDto> {

}