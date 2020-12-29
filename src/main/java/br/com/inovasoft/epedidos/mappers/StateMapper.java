package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.StateDto;
import br.com.inovasoft.epedidos.models.entities.State;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { CityMapper.class })
public interface StateMapper extends BaseMapper<State, StateDto> {

}