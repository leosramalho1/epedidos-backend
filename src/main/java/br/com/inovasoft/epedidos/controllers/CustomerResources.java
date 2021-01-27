package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.CustomerAddressDto;
import br.com.inovasoft.epedidos.models.dtos.CustomerDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.AccountToReceiveService;
import br.com.inovasoft.epedidos.services.CustomerAddressService;
import br.com.inovasoft.epedidos.services.CustomerService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Path("/customers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Customer")
public class CustomerResources {

    @Inject
    TokenService tokenService;

    @Inject
    CustomerService service;

    @Inject
    CustomerAddressService customerAddressService;

    @Inject
    AccountToReceiveService accountToReceiveService;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page) {
        return Response.status(Response.Status.OK).entity(service.listAll(page)).build();
    }

    @GET
    @Path("/{id}/billings")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAllCostumerBillingByCustomer(@QueryParam("page") int page, @PathParam("id") Long idCustomer) {

        PaginationDataResponse<CustomerDto> list;

        list = accountToReceiveService.buildAllByCustomer(page);

        return Response.status(Response.Status.OK).entity(list).build();
    }

    @GET
    @Path("/billings")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public PaginationDataResponse<CustomerDto> listAllCostumerBilling(
            @QueryParam("page") int page) {
        return accountToReceiveService.buildAllByCustomer(page);
    }

    @GET
    @Path("/suggestion")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response buyerSuggestion(@QueryParam("query") String query) {
        return Response.status(Response.Status.OK).entity(service.getSuggestions(query)).build();
    }

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Path("/select")
    public Response listActive() {
        return Response.status(Response.Status.OK).entity(service.listActive()).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long CustomerId) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(CustomerId)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid CustomerDto CustomerDto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(CustomerDto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long idCustomer, @Valid CustomerDto Customer)
            throws IllegalAccessException, InvocationTargetException {
        return Response.status(Response.Status.OK).entity(service.update(idCustomer, Customer)).build();
    }

    @DELETE
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{id}/addresses")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public List<CustomerAddressDto> getCustomerAddress(@PathParam("id") Long id) {
        return customerAddressService.findAddressesDtoById(id, tokenService.getSystemId());
    }

    @POST
    @Path("/{id}/addresses")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public CustomerAddressDto postCustomerAddress(@PathParam("id") Long idCustomer,
                                                  CustomerAddressDto customerAddressDto) {
        return customerAddressService.saveDto(idCustomer, customerAddressDto);
    }

    @PUT
    @Path("/{id}/addresses")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public CustomerAddressDto putCustomerAddress(@PathParam("id") Long idCustomer,
                                                 CustomerAddressDto customerAddressDto) {
        return customerAddressService.updateDto(idCustomer, customerAddressDto);
    }

    @DELETE
    @Path("/{id}/addresses/{idAddress}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public void deleteCustomerAddress(@PathParam("id") Long id, @PathParam("idAddress") Long idAddress) {
        customerAddressService.softDelete(idAddress, id);
    }

}
