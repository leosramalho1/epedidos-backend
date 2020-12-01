package br.com.inovasoft.epedidos.controllers;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.AccountToReceiveService;

@Path("/account-receive")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Account-Receive")
public class AccountToReceiveResources {

    @Inject
    AccountToReceiveService service;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page) {
        return Response.status(Response.Status.OK).entity(service.listAll(page)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long AccountToReceiveId) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(AccountToReceiveId)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid AccountToReceiveDto AccountToReceiveDto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(AccountToReceiveDto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long idAccountToReceive, @Valid AccountToReceiveDto AccountToReceive)
            throws IllegalAccessException, InvocationTargetException {
        return Response.status(Response.Status.OK).entity(service.update(idAccountToReceive, AccountToReceive)).build();
    }

    @DELETE
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
