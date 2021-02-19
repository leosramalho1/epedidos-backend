package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.SetupDto;
import br.com.inovasoft.epedidos.models.entities.Setup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface SetupMapper extends BaseMapper<Setup, SetupDto> {

}