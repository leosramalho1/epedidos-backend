package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;

import br.com.inovasoft.epedidos.models.dtos.CompanyDto;
import br.com.inovasoft.epedidos.models.entities.Company;

@Mapper(componentModel = "cdi")
public interface CompanyMapper extends BaseMapper<Company, CompanyDto> {

}