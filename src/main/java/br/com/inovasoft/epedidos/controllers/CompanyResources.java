package br.com.inovasoft.epedidos.controllers;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
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

import br.com.inovasoft.epedidos.models.dtos.CompanyDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.Company;
import br.com.inovasoft.epedidos.models.entities.CompanySystem;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.CompanyService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@Path("/companies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Admin")
public class CompanyResources {

    private static final int limitPerPage = 25;

    @Inject
    CompanyService service;

    @GET
    @RolesAllowed(JwtRoles.USER_ADMIN)
    public Response listAll(@QueryParam("page") int page) {
        PanacheQuery<Company> listCompanies = Company.find("select o from Company o order by o.name desc");

        List<Company> dataList = listCompanies.page(Page.of(page - 1, limitPerPage)).list();

        PaginationDataResponse<Company> result = new PaginationDataResponse<>(dataList, limitPerPage, (int) Company.count());
        return Response.status(Response.Status.OK).entity(result).build();

    }

    @GET
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_ADMIN)
    public Response getById(@PathParam("id") Long companyId) {
        return Response.status(Response.Status.OK).entity(service.findById(companyId)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_ADMIN)
    @Transactional
    public Response save(@Valid CompanyDto company) {

        CompanyDto entityResponse = service.saveDto(company);
        return Response.status(Response.Status.CREATED).entity(entityResponse).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_ADMIN)
    @Transactional
    public Response change(@PathParam("id") Long idCompany, @Valid CompanyDto company) {

        CompanyDto entityResponse = service.update(idCompany, company);

        return Response.status(Response.Status.OK).entity(entityResponse).build();
    }

    @DELETE
    @RolesAllowed(JwtRoles.USER_ADMIN)
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Company.deleteById(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Path("/{companyId}/systems/{systemId}")
    @RolesAllowed(JwtRoles.USER_ADMIN)
    public Response getSystemsById(@PathParam("companyId") Long companyId, @PathParam("systemId") Long systemId) {
        return Response.status(Response.Status.OK)
                .entity(CompanySystem.find("system.id=?1 and company.id=?2", systemId, companyId).firstResult())
                .build();
    }

    @GET
    @Path("/{companyId}/systems")
    @RolesAllowed(JwtRoles.USER_ADMIN)
    public Response listAllSystems(@PathParam("companyId") Long companyId) {
        return Response.status(Response.Status.OK).entity(CompanySystem.find("company.id", companyId).list()).build();
    }

}
