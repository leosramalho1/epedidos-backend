package br.com.inovasoft.epedidos.constraint;

import br.com.inovasoft.epedidos.constraint.validators.CpfCnpjValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CpfCnpjValidator.class)
public @interface CpfCnpj {
    String message() default "Cpf ou Cnpj deve ser válido.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
