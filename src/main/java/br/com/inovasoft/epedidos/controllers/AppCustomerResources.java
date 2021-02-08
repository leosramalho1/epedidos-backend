package br.com.inovasoft.epedidos.controllers;

import br.com.inovasoft.epedidos.models.dtos.LoginDto;
import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.entities.Company;
import br.com.inovasoft.epedidos.models.entities.CompanySystem;
import br.com.inovasoft.epedidos.models.entities.Customer;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.security.jwt.JwtRoles;
import br.com.inovasoft.epedidos.services.AccountToReceiveService;
import br.com.inovasoft.epedidos.services.CustomerService;
import br.com.inovasoft.epedidos.services.OrderService;
import br.com.inovasoft.epedidos.services.ProductService;
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
    CustomerService service;

    @Inject
    TokenService tokenService;

    @Inject
    OrderService orderService;
    
    @Inject
    ProductService productService;

    @Inject
    AccountToReceiveService accountToReceiveService;

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

        service.changePassword(user.getPassword(), user.getConfirmPassword());

        return Response.status(Response.Status.CREATED).build();

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

    @GET
    @Path("/orders")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    public Response listOrders() {
        return Response.status(Response.Status.OK).entity(orderService.listAllByCustomer()).build();
    }


    @GET
    @Path("/orders/{id}")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    public Response getOrderById(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(orderService.findDtoById(id)).build();
    }

    @POST
    @Path("/orders")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    @Transactional
    public Response save(@Valid OrderDto orderDto) {
        return Response.status(Response.Status.CREATED).entity(orderService.saveDtoFromApp(orderDto)).build();
    }

    @PUT
    @Path("/orders/{id}")
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    @Transactional
    public Response change(@PathParam("id") Long idOrder, @Valid OrderDto order)
            throws IllegalAccessException, InvocationTargetException {
        return Response.status(Response.Status.OK).entity(orderService.update(idOrder, order)).build();
    }

    @GET
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    @Path("/products")
    public Response listToGrid() {
        return Response.status(Response.Status.OK).entity(productService.listProductsToGrid()).build();
    }

    @GET
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    @Path("/billings")
    public Response listBillings() {
        return Response.status(Response.Status.OK).entity(accountToReceiveService.listBillings()).build();
    }

    @GET
    @RolesAllowed(JwtRoles.USER_APP_CUSTOMER)
    @Path("/billings/{id}/orders")
    public Response listToGrid(@PathParam("id") Long idAccountToReceive) {
        return Response.status(Response.Status.OK).entity(orderService.listAllByCustomerAndAccounttoReceive(idAccountToReceive)).build();
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
