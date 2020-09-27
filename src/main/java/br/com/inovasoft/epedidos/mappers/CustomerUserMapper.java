package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.CustomerUserDto;
import br.com.inovasoft.epedidos.models.entities.CustomerUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { CategoryMapper.class })
public interface CustomerUserMapper extends BaseMapper<CustomerUser, CustomerUserDto> {

}