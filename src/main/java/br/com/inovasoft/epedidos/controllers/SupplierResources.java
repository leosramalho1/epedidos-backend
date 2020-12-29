package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.SupplierAddressDto;
import br.com.inovasoft.epedidos.models.dtos.SupplierDto;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.SupplierAddressService;
import br.com.inovasoft.epedidos.services.SupplierService;
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

@Path("/suppliers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Supplier")
public class SupplierResources {

    @Inject
    SupplierService service;

    @Inject
    SupplierAddressService supplierAddressService;

    @Inject
    TokenService tokenService;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page) {
        return Response.status(Response.Status.OK).entity(service.listAll(page)).build();
    }

    @GET
    @Path("/suggestion")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response buyerSuggestion(@QueryParam("query") String query) {
        return Response.status(Response.Status.OK).entity(service.getSuggestions(query)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long SupplierId) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(SupplierId)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid SupplierDto SupplierDto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(SupplierDto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long idSupplier, @Valid SupplierDto Supplier)
            throws IllegalAccessException, InvocationTargetException {
        return Response.status(Response.Status.OK).entity(service.update(idSupplier, Supplier)).build();
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
    public List<SupplierAddressDto> getCustomerAddress(@PathParam("id") Long id) {
        return supplierAddressService.findAddressesDtoById(id, tokenService.getSystemId());
    }

    @POST
    @Path("/{id}/addresses")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public SupplierAddressDto postCustomerAddress(@PathParam("id") Long idCustomer,
                                                  SupplierAddressDto supplierAddressDto) {
        return supplierAddressService.saveDto(idCustomer, supplierAddressDto);
    }

    @PUT
    @Path("/{id}/addresses")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public SupplierAddressDto putCustomerAddress(@PathParam("id") Long idCustomer,
                                                 SupplierAddressDto supplierAddressDto) {
        return supplierAddressService.updateDto(supplierAddressDto);
    }

    @DELETE
    @Path("/{id}/addresses/{idAddress}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public void deleteCustomerAddress(@PathParam("id") Long id, @PathParam("idAddress") Long idAddress) {
        supplierAddressService.softDelete(idAddress, id);
    }

}
