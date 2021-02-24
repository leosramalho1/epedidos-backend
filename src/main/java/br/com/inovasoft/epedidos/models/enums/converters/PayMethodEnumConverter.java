package br.com.inovasoft.epedidos.models.enums.converters;

import br.com.inovasoft.epedidos.models.enums.PayMethodEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PayMethodEnumConverter implements AttributeConverter<PayMethodEnum, String> {

    @Override
    public String convertToDatabaseColumn(PayMethodEnum payMethod) {
        if (payMethod == null) {
            return null;
        }
        return payMethod.getDescription();
    }

    @Override
    public PayMethodEnum convertToEntityAttribute(String payMethod) {
        if (payMethod == null) {
            return null;
        }

        return PayMethodEnum.fromValue(payMethod);
    }
}