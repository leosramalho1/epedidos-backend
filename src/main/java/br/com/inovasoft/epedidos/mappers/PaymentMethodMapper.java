package br.com.inovasoft.epedidos.mappers;

import org.mapstruct.Mapper;

import br.com.inovasoft.epedidos.models.dtos.PaymentMethodDto;
import br.com.inovasoft.epedidos.models.entities.references.PaymentMethod;

@Mapper(componentModel = "cdi")
public interface PaymentMethodMapper extends BaseMapper<PaymentMethod, PaymentMethodDto> {


}