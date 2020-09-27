package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;

import br.com.inovasoft.epedidos.models.dtos.CompanySystemDto;
import br.com.inovasoft.epedidos.models.entities.CompanySystem;

@Mapper(componentModel = "cdi")
public interface CompanySystemMapper extends BaseMapper<CompanySystem, CompanySystemDto> {

}