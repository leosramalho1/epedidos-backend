package br.com.inovasoft.epedidos.constraint.validators;

import br.com.inovasoft.epedidos.constraint.CpfCnpj;
import br.com.inovasoft.epedidos.util.ValidaCpfCnpjUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CpfCnpjValidator implements ConstraintValidator<CpfCnpj, String> {

    @Override
    public boolean isValid(String cpfCnpj, ConstraintValidatorContext context) {
        return ValidaCpfCnpjUtil.isCPFValido(cpfCnpj) || ValidaCpfCnpjUtil.isCNPJValido(cpfCnpj);
    }

}
