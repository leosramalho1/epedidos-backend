package br.com.inovasoft.epedidos.controllers;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.models.entities.Systems;

@Path("/systems")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Admin")
public class SystemResources {

    @GET
    @RolesAllowed(JwtRoles.USER_ADMIN)
    public Response listAll() {
        return Response.status(Response.Status.OK).entity(Systems.listAll()).build();
    }
}
