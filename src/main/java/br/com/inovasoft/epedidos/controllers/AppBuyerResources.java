package br.com.inovasoft.epedidos.controllers;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
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

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.inovasoft.epedidos.models.dtos.LoginDto;
import br.com.inovasoft.epedidos.models.dtos.PurchaseAppDto;
import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.models.dtos.SupplierDto;
import br.com.inovasoft.epedidos.models.entities.Company;
import br.com.inovasoft.epedidos.models.entities.CompanySystem;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.PurchaseService;
import br.com.inovasoft.epedidos.services.SupplierService;
import br.com.inovasoft.epedidos.services.UserService;

@Path("/app/buyers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "User")
public class AppBuyerResources {

    @Inject
    UserService service;

    @Inject
    PurchaseService purchaseService;

    @Inject
    SupplierService supplierService;

    @Inject
    TokenService tokenService;

    @PUT
    @Path("/changePass")
    @RolesAllowed(JwtRoles.USER_APP_BUYER)
    public Response changePass(UserPortal user) {
        if (user.getPassword() == null) {
            throw new WebApplicationException(Response.status(400).entity("Nova senha é obrigatório").build());
        }
        if (user.getConfirmPassword() == null) {
            throw new WebApplicationException(Response.status(400).entity("Confirmação senha é obrigatório").build());
        }

        service.changePassword(user.getPassword(), user.getConfirmPassword());

        return Response.status(Response.Status.CREATED).build();

    }

    @POST
    @Path("/login")
    @Operation(operationId = "login")
    @PermitAll
    public LoginDto login(LoginDto login) {
        UserPortal existingUser = UserPortal.find("email", login.getEmail()).firstResult();

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
        login.setToken(tokenService.generateAppBuyerToken(existingUser.getEmail(),
                company.getId(), system.getSystemKey()));

        return login;
    }

    @GET
    @Path("/purchases/prepare")
    @RolesAllowed(JwtRoles.USER_APP_BUYER)
    public Response prepareNewPurchase() {
        UserPortal userPortal = UserPortal.find("email", tokenService.getUserEmail()).firstResult();
        return Response.status(Response.Status.OK).entity(purchaseService.getOpenOrderAndGroupByIdBuyer(userPortal.getId())).build();
    }

    @GET
    @Path("/purchases")
    @RolesAllowed(JwtRoles.USER_APP_BUYER)
    public Response listPurchase() {
        return Response.status(Response.Status.OK).entity(purchaseService.listPurchasesByBuyer()).build();
    }


    @GET
    @Path("/purchases/{id}")
    @RolesAllowed(JwtRoles.USER_APP_BUYER)
    public Response getPurchaseById(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(purchaseService.findAppDtoById(id)).build();
    }

    @POST
    @Path("/purchases")
    @RolesAllowed(JwtRoles.USER_APP_BUYER)
    @Transactional
    public Response save(@Valid PurchaseAppDto purchaseDto) {
        return Response.status(Response.Status.CREATED).entity(purchaseService.saveDtoFromApp(purchaseDto)).build();
    }

    @PUT
    @Path("/purchases/{id}")
    @RolesAllowed(JwtRoles.USER_APP_BUYER)
    @Transactional
    public Response change(@PathParam("id") Long idPurchase, @Valid PurchaseDto purchaseDto)
            throws IllegalAccessException, InvocationTargetException {
        return Response.status(Response.Status.OK).entity(purchaseService.update(idPurchase, purchaseDto)).build();
    }

    @GET
    @Path("/suppliers/suggestion")
    @RolesAllowed(JwtRoles.USER_APP_BUYER)
    public Response buyerSuggestion(@QueryParam("query") String query) {
        return Response.status(Response.Status.OK).entity(supplierService.getSuggestions(query)).build();
    }

    @POST
    @RolesAllowed(JwtRoles.USER_APP_BUYER)
    @Path("/suppliers")
    @Transactional
    public Response save(@Valid SupplierDto SupplierDto) {
        return Response.status(Response.Status.CREATED).entity(supplierService.saveDto(SupplierDto)).build();
    }
}
