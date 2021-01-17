package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.AccountToPayDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.AccountToPayService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("/account-pay")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Account-Pay")
public class AccountToPayResources {

    @Inject
    AccountToPayService service;

    @GET
//    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page, @QueryParam("status") List<PayStatusEnum> status,
                            @QueryParam("supplier") Long supplier, @QueryParam("dueDateMin") String dueDateMin,
                            @QueryParam("dueDateMax") String dueDateMax, @QueryParam("paidOutDateMin") String paidOutDateMin,
                            @QueryParam("paidOutDateMax") String paidOutDateMax) {
        PaginationDataResponse<AccountToPayDto> list;

        list = service.listAll(page, status, supplier, null,
                Optional.ofNullable(dueDateMin).map(LocalDate::parse).orElse(null),
                Optional.ofNullable(dueDateMax).map(LocalDate::parse).orElse(null),
                Optional.ofNullable(paidOutDateMin).map(LocalDate::parse).orElse(null),
                Optional.ofNullable(paidOutDateMax).map(LocalDate::parse).orElse(null)
        );


        return Response.status(Response.Status.OK).entity(list).build();
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
    public Response save(@Valid AccountToPayDto accountToPayDto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(accountToPayDto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long id, @Valid AccountToPayDto accountToPay) {
        return Response.status(Response.Status.OK).entity(service.update(id, accountToPay)).build();
    }

    @PUT
    @Path("/{id}/pay")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response pay(@PathParam("id") Long id, @Valid AccountToPayDto accountToPay) {
        return Response.status(Response.Status.OK).entity(service.pay(id, accountToPay)).build();
    }

}
