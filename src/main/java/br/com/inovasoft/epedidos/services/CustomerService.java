package br.com.inovasoft.epedidos.services;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import br.com.inovasoft.epedidos.mappers.CustomerMapper;
import br.com.inovasoft.epedidos.models.dtos.CustomerDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.Customer;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class CustomerService extends BaseService<Customer> {

    @Inject
    TokenService tokenService;

    @Inject
    CustomerMapper mapper;

    @Inject
    CustomerAddressService addressService;

    public PaginationDataResponse<CustomerDto> listAll(int page) {
        PanacheQuery<Customer> listCustomers = Customer.find(
                "select p from Customer p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Customer> dataList = listCustomers.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), LIMIT_PER_PAGE, (int) Customer.count());
    }

    public List<CustomerDto> listActive() {

        List<Customer> dataList = Customer
                .find("select p from Customer p where p.systemId = ?1 and p.deletedOn is null order by p.name",
                        tokenService.getSystemId())
                .list();

        return mapper.toDto(dataList);
    }

    public List<CustomerDto> getSuggestions(String query) {
        List<Customer> dataList = Customer.list(
                "systemId = ?1 and upper(name) like ?2 and status = ?3 and deletedOn is null", Sort.by("name"),
                tokenService.getSystemId(), "%" + query.toUpperCase() + "%", StatusEnum.ACTIVE);

        return mapper.toDto(dataList);
    }

    public Customer findById(Long id) {
        return Customer.find("select p from Customer p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public CustomerDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public CustomerDto saveDto(CustomerDto dto) {

        // Check if exists cpf or email
        if (Customer.count("cpfCnpj=?1 and deletedOn is null", dto.getCpfCnpj()) > 0)
            throw new WebApplicationException(Response.status(400)
                    .entity("Atenção, já existe um cliente cadastrado com o CPF/CNPJ, favor informar outro.").build());

        if (Customer.count("email=?1 and deletedOn is null", dto.getEmail()) > 0)
            throw new WebApplicationException(Response.status(400)
                    .entity("Atenção, já existe um cliente cadastrado com o E-mail, favor informar outro.").build());

        Customer entity = mapper.toEntity(dto);
        entity.setPassword(entity.getCpfCnpj());
        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        // Save new address
        dto.getAddress().stream().filter(item -> Objects.isNull(item.getId())).forEach(dtoAddress -> {

            addressService.saveDto(entity.getId(), dtoAddress);
        });

        return mapper.toDto(entity);
    }

    public CustomerDto update(Long id, CustomerDto dto) {

        // Check if exists cpf or email
        if (Customer.count("cpfCnpj=?1 and id !=?2  and deletedOn is null", dto.getCpfCnpj(), id) > 0)
            throw new WebApplicationException(Response.status(400)
                    .entity("Atenção, já existe um cliente cadastrado com o CPF/CNPJ, favor informar outro.").build());

        if (Customer.count("email=?1 and id !=?2 and deletedOn is null", dto.getEmail(), id) > 0)
            throw new WebApplicationException(Response.status(400)
                    .entity("Atenção, já existe um cliente cadastrado com o E-mail, favor informar outro.").build());

        Customer entity = findById(id);

        mapper.updateEntityFromDto(dto, entity);

        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        Customer.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

    @Transactional
    public void changePassword(String pass, String confirmPass) {
        if (!pass.equals(confirmPass)) {
            throw new WebApplicationException(
                    Response.status(400).entity("Confirmação senha deve ser igual a senha.").build());
        }
        Customer customer = Customer.find("cpfCnpj", tokenService.getJsonWebToken().getSubject()).firstResult();
        customer.setPassword(pass);
        customer.persist();
    }
}