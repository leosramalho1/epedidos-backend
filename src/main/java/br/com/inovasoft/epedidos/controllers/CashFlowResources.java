package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.CashFlowService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.Optional;

@Path("/cash-flow")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Cash-Flow")
public class CashFlowResources {

    @Inject
    CashFlowService service;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page, @QueryParam("paidOutDateMin") String paidOutDateMin,
                            @QueryParam("paidOutDateMax") String paidOutDateMax) {
        return Response.status(Response.Status.OK).entity(service.listAll(page,
                Optional.ofNullable(paidOutDateMin).map(LocalDate::parse).orElse(null),
                Optional.ofNullable(paidOutDateMax).map(LocalDate::parse).orElse(null))
        ).build();
    }


}
