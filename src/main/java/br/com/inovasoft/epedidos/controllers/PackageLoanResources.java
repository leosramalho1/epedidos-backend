package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.PackageLoanDto;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.PackageLoanService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/package-loan")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Package Loan")
public class PackageLoanResources {

    @Inject
    PackageLoanService service;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page) {
        return Response.status(Response.Status.OK).entity(service.listAll(page)).build();
    }

    @GET
    @Path("/pending")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAllOpen(@QueryParam("page") int page,
                                @QueryParam("responsibleName") String responsibleName,
                                @QueryParam("responsibleType") String responsibleType) {
        return Response.status(Response.Status.OK).entity(service.listAllPending(page, responsibleName, responsibleType)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response get(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(id)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response update(@PathParam("id") Long id, PackageLoanDto dto) {
        return Response.status(Response.Status.OK).entity(service.update(id, dto)).build();
    }

    @PUT
    @Path("/{id}/finalize")
    @Transactional
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response finalizeLoan(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(service.finalizeLoan(id)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response delete(@PathParam("id") Long id) {
        service.softDelete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
