package br.com.inovasoft.epedidos.controllers;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.models.entities.UserAdmin;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.services.AdminService;

@Path("/administrators")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Admin")
public class UserAdminResources {

    @Inject
    AdminService service;

    @Inject
    TokenService tokenService;

    @Inject
    JsonWebToken jwt;

    @POST
    @RolesAllowed(JwtRoles.USER_ADMIN)
    @Transactional
    public Response save(@Valid UserAdmin user) {
        UserAdmin.persist(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/changePass")
    @RolesAllowed(JwtRoles.USER_ADMIN)
    public Response changePass(UserPortal user) {
        if (user.getPassword() == null) {
            throw new WebApplicationException(Response.status(400).entity("Nova senha é obrigatório").build());
        }
        if (user.getConfirmPassword() == null) {
            throw new WebApplicationException(Response.status(400).entity("Confirmação senha é obrigatório").build());
        }
        if (!user.getConfirmPassword().equals(user.getPassword())) {
            throw new WebApplicationException(
                    Response.status(400).entity("Confirmação senha deve ser igual a senha.").build());
        }
        service.changePassword(jwt.getSubject(), user.getPassword());

        return Response.status(Response.Status.CREATED).build();

    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(JwtRoles.USER_ADMIN)
    public Response delete(@PathParam("id") Long id) {
        service.softDelete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/validateToken")
    @Operation(operationId = "validateToken")
    @PermitAll
    public UserPortal validateToken(UserPortal user) {
        if (user.getToken() == null) {
            return null;
        }
        boolean validToken = tokenService.validateToken(user.getToken());
        if (!validToken) {
            return null;
        }
        return user;
    }

    @POST
    @Path("/login")
    @Operation(operationId = "login")
    @PermitAll
    public UserAdmin login(UserAdmin user) {
        UserAdmin existingUser = UserAdmin.find("email", user.getEmail()).firstResult();
        if (existingUser == null) {
            throw new WebApplicationException(Response.status(403).entity("Usuário ou senha inválido!").build());
        }
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new WebApplicationException(Response.status(403).entity("Usuário ou senha inválido!").build());
        }
        existingUser.setToken(tokenService.generateAdminToken(existingUser.getEmail(), user.getName()));
        return existingUser;
    }
}
