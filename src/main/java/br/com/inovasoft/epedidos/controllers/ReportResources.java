package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.services.ReportService;
import com.itextpdf.text.DocumentException;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Path("/reports")
@Tag(name = "Report")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ReportResources {

    @Inject
    ReportService service;

    @GET
    @Path("/orders/closed")
//    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Produces(value = MediaType.APPLICATION_OCTET_STREAM)
    public Response get(@QueryParam("customer") Long customerId, @QueryParam("initDate") String initDate) throws DocumentException, CssResolverException, IOException {

        return Response.ok(service.products(customerId, Optional.ofNullable(initDate).map(LocalDate::parse).orElse(null)), MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .header("Content-Disposition", "attachment;filename=orders-closed.pdf")
                .build();
    }



}
