package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.AccountToReceiveService;
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

@Path("/account-receive")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Account-Receive")
public class AccountToReceiveResources {

    @Inject
    AccountToReceiveService service;

    @GET
//    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page, @QueryParam("status") List<PayStatusEnum> status,
                            @QueryParam("customer") Long customer, @QueryParam("dateMin") String dateMin,
                            @QueryParam("dateMax") String dateMax) {
        PaginationDataResponse<AccountToReceiveDto> list;

        list = service.listAll(page, status, null, customer,
                Optional.ofNullable(dateMin).map(LocalDate::parse).orElse(null),
                Optional.ofNullable(dateMax).map(LocalDate::parse).orElse(null)
        );

        return Response.status(Response.Status.OK).entity(list).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long accountToReceiveId) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(accountToReceiveId)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid AccountToReceiveDto accountToReceiveDto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(accountToReceiveDto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response pay(@PathParam("id") Long idAccountToReceive, @Valid AccountToReceiveDto accountToReceiveDto) {
        return Response.status(Response.Status.OK).entity(service.update(idAccountToReceive, accountToReceiveDto)).build();
    }

    @PUT
    @Path("/{id}/receive")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long id, @Valid AccountToReceiveDto accountToReceiveDto) {
        return Response.status(Response.Status.OK).entity(service.receive(id, accountToReceiveDto)).build();
    }

}
