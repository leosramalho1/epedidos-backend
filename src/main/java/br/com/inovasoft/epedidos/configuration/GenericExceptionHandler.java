package br.com.inovasoft.epedidos.configuration;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Provider
public class GenericExceptionHandler implements ExceptionMapper<ArcUndeclaredThrowableException> {

    @Override
    public Response toResponse(ArcUndeclaredThrowableException exception) {
        log.error("Erro ao realizar a transação", exception);

        RollbackException cause = (RollbackException) exception.getCause();
        if(cause.getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) cause.getCause();
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();

            Set<String> messages = new HashSet<>(constraintViolations.size());
            messages.addAll(constraintViolations.stream()
                    .map(constraintViolation -> String.format("%s valor inválido. %s",
                            constraintViolation.getInvalidValue(),
                            constraintViolation.getMessage()))
                    .collect(Collectors.toList()));

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(messages).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(cause.getCause().getMessage()).build();
        }
    }

}