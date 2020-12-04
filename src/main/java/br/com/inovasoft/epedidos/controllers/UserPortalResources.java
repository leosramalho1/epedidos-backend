package br.com.inovasoft.epedidos.controllers;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.security.PermitAll;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.inovasoft.epedidos.models.entities.Company;
import br.com.inovasoft.epedidos.models.entities.CompanySystem;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.UserService;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "User")
public class UserPortalResources {

    @Inject
    UserService service;

    @Inject
    TokenService tokenService;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response listAll(@QueryParam("page") int page) {
        return Response.status(Response.Status.OK).entity(service.listAll(page)).build();
    }

    @GET
    @Path("buyers/suggestion")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response buyerSuggestion(@QueryParam("query") String query) {
        return Response.status(Response.Status.OK).entity(service.getSuggestions(query)).build();
    }

    @GET
    @Path("buyers/select-options")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response buyerOptions() {
        return Response.status(Response.Status.OK).entity(service.getListAllOptions()).build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    public Response getById(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(UserPortal.findById(id)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response change(@PathParam("id") Long iduser, @Valid UserPortal user)
            throws IllegalAccessException, InvocationTargetException {
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
        return Response.status(Response.Status.OK).entity(service.update(iduser, user)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response save(@Valid UserPortal user) {

        service.save(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/changePass")
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
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
    @RolesAllowed(JwtRoles.USER_BACKOFFICE)
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        service.softDelete(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/register")
    @Operation(operationId = "register")
    @PermitAll
    public UserPortal register(UserPortal user) {

        UserPortal existingUser = UserPortal.find("email", user.getEmail()).firstResult();
        if (existingUser != null) {
            throw new WebApplicationException(
                    Response.status(400).entity("Já existe um usuário cadastrado com essa senha.").build());
        }
        service.save(user);
        return user;
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
    public UserPortal login(UserPortal user) {
        UserPortal existingUser = UserPortal.find("email", user.getEmail()).firstResult();
        if (existingUser == null) {
            throw new WebApplicationException(Response.status(403).entity("Usuário ou senha inválido!").build());
        }
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new WebApplicationException(Response.status(403).entity("Usuário ou senha inválido!").build());
        }
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new WebApplicationException(Response.status(403).entity("Usuário ou senha inválido!").build());
        }

        CompanySystem system = CompanySystem.findById(existingUser.getSystemId());

        if (!system.getStatus().equals("A")) {
            throw new WebApplicationException(Response.status(403)
                    .entity("Sistema bloqueado, favor entrar em contato com o administrador para maiores detalhes!")
                    .build());
        }

        Company company = system.getCompany();
        existingUser.setToken(tokenService.generateBackofficeToken(existingUser.getEmail(), user.getPassword(),
                company.getId(), system.getSystemKey()));

        return existingUser;
    }
}
