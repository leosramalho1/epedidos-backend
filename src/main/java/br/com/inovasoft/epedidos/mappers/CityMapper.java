package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.CityDto;
import br.com.inovasoft.epedidos.models.entities.City;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { StateMapper.class })
public interface CityMapper extends BaseMapper<City, CityDto> {

}