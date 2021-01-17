package br.com.inovasoft.epedidos.models.enums.converters;

import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PayStatusEnumConverter implements AttributeConverter<PayStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(PayStatusEnum payStatus) {
        if (payStatus == null) {
            return null;
        }
        return payStatus.getDescription();
    }

    @Override
    public PayStatusEnum convertToEntityAttribute(String payStatus) {
        if (payStatus == null) {
            return null;
        }

        return PayStatusEnum.fromValue(payStatus);
    }
}