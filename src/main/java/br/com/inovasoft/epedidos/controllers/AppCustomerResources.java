package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.LoginDto;
import br.com.inovasoft.epedidos.models.entities.Company;
import br.com.inovasoft.epedidos.models.entities.CompanySystem;
import br.com.inovasoft.epedidos.models.entities.Customer;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.UserService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;

@Path("/app/customer")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "App Customer")
public class AppCustomerResources {

    @Inject
    UserService service;

    @Inject
    TokenService tokenService;


    @GET
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    public Response listAll(@QueryParam("page") int page) {
        return Response.status(Response.Status.OK).entity(service.listAll(page)).build();
    }

    @GET
    @Path("buyers/suggestion")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    public Response buyerSuggestion(@QueryParam("query") String query) {
        return Response.status(Response.Status.OK).entity(service.getSuggestions(query)).build();
    }

    @GET
    @Path("buyers/select-options")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    public Response buyerOptions() {
        return Response.status(Response.Status.OK).entity(service.getListAllOptions()).build();
    }

    @GET
    @Path("{id}")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    public Response getById(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(UserPortal.findById(id)).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    @Transactional
    public Response change(@PathParam("id") Long iduser, @Valid UserPortal user)
            throws IllegalAccessException, InvocationTargetException {
        if (user.getPassword() == null) {
            throw new WebApplicationException(Response.status(400).entity("Nova senha é obrigatório").build());
        }
        if (user.getConfirmPassword() == null) {
            throw new WebApplicationException(Response.status(400).entity("Confirmação senha é obrigatório").build());
        }

        return Response.status(Response.Status.OK).entity(service.update(iduser, user)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    @Transactional
    public Response save(@Valid UserPortal user) {

        if (!user.getConfirmPassword().equals(user.getPassword())) {
            throw new WebApplicationException(
                    Response.status(400).entity("Confirmação senha deve ser igual a senha.").build());
        }
        service.save(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/changePass")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    public Response changePass(UserPortal user) {
        if (user.getPassword() == null) {
            throw new WebApplicationException(Response.status(400).entity("Nova senha é obrigatório").build());
        }
        if (user.getConfirmPassword() == null) {
            throw new WebApplicationException(Response.status(400).entity("Confirmação senha é obrigatório").build());
        }

        service.changePassword(user);

        return Response.status(Response.Status.CREATED).build();

    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    @Transactional
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
    public LoginDto login(LoginDto login) {
        Customer existingUser = Customer.find("cpfCnpj", login.getCpfCnpj()).firstResult();

        if (existingUser == null) {
            throw new WebApplicationException(Response.status(403).entity("Usuário ou senha inválido!").build());
        }
        if (!existingUser.getPassword().equals(login.getPassword())) {
            throw new WebApplicationException(Response.status(403).entity("Usuário ou senha inválido!").build());
        }
        if(existingUser.getStatus() != StatusEnum.ACTIVE){
            throw new WebApplicationException(Response.status(403).entity("O usuário inativo no sistema, entre em contato com o administrador.").build());
        }

        CompanySystem system = CompanySystem.findById(existingUser.getSystemId());

        if (!system.getStatus().equals("A")) {
            throw new WebApplicationException(Response.status(403)
                    .entity("Sistema bloqueado, favor entrar em contato com o administrador para maiores detalhes!")
                    .build());
        }

        Company company = system.getCompany();
        login.setPassword(null);
        login.setUserName(existingUser.getName());
        login.setToken(tokenService.generateAppCustomerToken(existingUser.getCpfCnpj(), existingUser.getName(),
                company.getId(), system.getSystemKey()));

        return login;
    }
}
