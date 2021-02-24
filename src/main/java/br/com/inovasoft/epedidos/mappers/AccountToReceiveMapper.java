package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.entities.AccountToReceive;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi", uses = { CustomerMapper.class })
public interface AccountToReceiveMapper extends BaseMapper<AccountToReceive, AccountToReceiveDto> {


}