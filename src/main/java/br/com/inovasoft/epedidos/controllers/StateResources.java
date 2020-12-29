package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.StateDto;
import br.com.inovasoft.epedidos.models.entities.State;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.CityService;
import br.com.inovasoft.epedidos.services.StateService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/states")
@Tag(name = "State")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StateResources {

    @Inject
    StateService service;

    @Inject
    CityService cityService;
    
    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll() {
        return Response.status(Response.Status.OK).entity(service.listAllDto()).build();
    }

    @GET
    @Path("/{idState}/cities")
    public Response getCitiesByIdState(@PathParam("idState") Long idState) {
        return Response.status(Response.Status.OK).entity(cityService.listAllDtoByState(idState)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(id)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid StateDto dto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(dto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long id, @Valid StateDto dto) {
        return Response.status(Response.Status.OK).entity(service.update(id, dto)).build();
    }

    @DELETE
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        State.deleteById(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
