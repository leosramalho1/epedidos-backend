package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.UserPortalDto;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserPortalMapper extends BaseMapper<UserPortal, UserPortalDto> {

}