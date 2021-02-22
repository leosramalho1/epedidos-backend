package br.com.inovasoft.epedidos.configuration;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Slf4j
@Provider
public class GenericExceptionHandler implements ExceptionMapper<Exception> {

    private static final String ERROR_MESSAGE = "Não foi possível concluir a solicitação.";
    @Override
    public Response toResponse(Exception exception) {
        log.error("Erro ao realizar a transação", exception);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ERROR_MESSAGE).build();

    }

}