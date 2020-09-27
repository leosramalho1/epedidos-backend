package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;

import br.com.inovasoft.epedidos.models.dtos.CategoryDto;
import br.com.inovasoft.epedidos.models.entities.references.Category;

@Mapper(componentModel = "cdi")
public interface CategoryMapper extends BaseMapper<Category, CategoryDto> {


}