package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.CustomerBillingDto;
import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.entities.views.ProductMap;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.OrderMapService;
import br.com.inovasoft.epedidos.services.OrderService;
import br.com.inovasoft.epedidos.services.PurchaseDistributionService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Orders")
public class OrderResources {

    @Inject
    OrderService service;

    @Inject
    OrderMapService orderMapService;

    @Inject
    PurchaseDistributionService purchaseDistributionService;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page, @QueryParam("status") List<OrderEnum> orderEnums) {
        return Response.status(Response.Status.OK).entity(service.listAll(page, orderEnums)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long orderId) {
        return Response.status(Response.Status.OK).entity(service.findDtoById(orderId)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid OrderDto orderDto) {
        return Response.status(Response.Status.CREATED).entity(service.saveDto(orderDto)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long idOrder, @Valid OrderDto order) {
        return Response.status(Response.Status.OK).entity(service.update(idOrder, order)).build();
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
    @Path("/distributions")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAllDistributions(@QueryParam("page") int page, @QueryParam("category") Long category) {
        return Response.status(Response.Status.OK)
                .entity(orderMapService.listAllDistributions(page, Optional.ofNullable(category))).build();
    }

    @PUT
    @Path("/distributions")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public void update(List<ProductMap> produtosCorrecao) {
        orderMapService.update(produtosCorrecao);
    }

    @POST
    @Path("/scheduler")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response scheduler() {
        return Response.status(Response.Status.OK).entity(service.changeOrdersToStatusPurchase()).build();
    }

    @GET
    @Path("/closing")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getClosing(@QueryParam("page") int page) {
        return Response.status(Response.Status.OK)
                .entity(purchaseDistributionService.buildAllByCustomer(page, null, OrderEnum.DISTRIBUTED)).build();
    }

    @POST
    @Path("/closing")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response postClosing(List<CustomerBillingDto> customerBillingDtos) {
        service.closeOrders(customerBillingDtos);
        return Response.status(Response.Status.OK).build();
    }
}
