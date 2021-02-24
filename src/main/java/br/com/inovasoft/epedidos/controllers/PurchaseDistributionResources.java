package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.BillingClosingDto;
import br.com.inovasoft.epedidos.models.dtos.PurchaseDistributionDto;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.PurchaseDistributionService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/purchase-distribution")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "PurchaseDistribution")
public class PurchaseDistributionResources {

    @Inject
    PurchaseDistributionService service;

    @POST
    @Transactional
    @Path("/invoice")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response invoice(BillingClosingDto billingClosing) {
        return Response.status(Response.Status.CREATED)
                .entity(service.invoice(billingClosing))
                .build();
    }

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page, @QueryParam("customer") Long idCustomer,
                            @QueryParam("createdOnMin") String createdOnMin, @QueryParam("createdOnMax") String createdOnMax) {
        return Response.status(Response.Status.OK).entity(service.buildAllByCustomer(page, idCustomer, OrderEnum.FINISHED)).build();
    }

    @PUT
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response changeValueCharged(List<PurchaseDistributionDto> purchaseDistributions) {
        service.update(purchaseDistributions);
        return Response.status(Response.Status.OK).build();
    }


}
