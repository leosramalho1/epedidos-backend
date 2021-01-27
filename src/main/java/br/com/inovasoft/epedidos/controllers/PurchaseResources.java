package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.PurchaseService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/purchase")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Purchase")
public class PurchaseResources {

    @Inject
    PurchaseService service;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page, @QueryParam("supplier") Long idSupplier, @QueryParam("idBuyer") Long idBuyer) {
        return Response.status(Response.Status.OK).entity(service.listAll(page, idSupplier, idBuyer)).build();
    }

    @GET
    @Path("/group/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getOpenOrderAndGroupByIdBuyer(@PathParam("id") Long buyerId) {
        return Response.status(Response.Status.OK).entity(service.getOpenOrderAndGroupByIdBuyer(buyerId)).build();
    }

    @GET
    @Path("/products/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getProductsToBuy(@PathParam("id") Long buyerId) {
        return Response.status(Response.Status.OK).entity(service.getProductsToBuy(buyerId)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long purchaseId) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(purchaseId)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid PurchaseDto purchaseDto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(purchaseDto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long idPurchase, @Valid PurchaseDto purchase) {
        return Response.status(Response.Status.OK).entity(service.update(idPurchase, purchase)).build();
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
