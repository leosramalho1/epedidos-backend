package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.entities.views.ProdutoCorrecao;
import br.com.inovasoft.epedidos.services.MapaCorrecaoService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/mapa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Mapa Correcao")
public class MapaCorrecaoResources {

    @Inject
    MapaCorrecaoService service;

    @GET
//    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page, @QueryParam("category") Long category) {
        return Response.status(Response.Status.OK).entity(service.listAll(page, Optional.ofNullable(category))).build();
    }

    @PUT
    @Transactional
//    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response update(List<ProdutoCorrecao> produtosCorrecao) {
        return Response.status(Response.Status.OK).entity(service.update(produtosCorrecao)).build();
    }
}
