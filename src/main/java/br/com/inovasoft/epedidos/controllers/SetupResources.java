package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.SetupDto;
import br.com.inovasoft.epedidos.models.enums.SetupEnum;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.SetupService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.quartz.SchedulerException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/companies/setups")
@Tag(name = "Setup")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SetupResources {

    @Inject
    SetupService service;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response get(@QueryParam("key") List<SetupEnum> keys) {
        return Response.status(Response.Status.OK).entity(service.listAll(keys)).build();
    }

    @GET
    @Path("/{key}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getByKey(@PathParam("key") SetupEnum key) {
        return Response.status(Response.Status.OK).entity(service.findDtoByKey(key)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid SetupDto dto) throws SchedulerException {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(dto)).build();
    }

    @PUT
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@Valid SetupDto dto) throws SchedulerException {
        return Response.status(Response.Status.OK).entity(service.update(dto)).build();
    }


}
