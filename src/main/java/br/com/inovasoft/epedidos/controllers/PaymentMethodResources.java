package br.com.inovasoft.epedidos.controllers;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
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

import br.com.inovasoft.epedidos.models.dtos.PaymentMethodDto;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.PaymentMethodService;

@Path("/companies/payment-methods")
@Tag(name = "Payment Method")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PaymentMethodResources {

    @Inject
    PaymentMethodService service;

    @GET
    @RolesAllowed({ JwtRoles.USER_BACKOFFICE, JwtRoles.USER_APP_BUYER })
    public Response get(@QueryParam("page") Integer page, @QueryParam("status") StatusEnum status) {
        return Response.status(Response.Status.OK).entity(service.listAll(page, status)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(id)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response save(@Valid PaymentMethodDto dto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(dto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response change(@Valid PaymentMethodDto dto, @PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(service.update(id, dto)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response delete(@PathParam("id") Long id) {
        service.softDelete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }


}
