package br.com.inovasoft.epedidos.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import br.com.inovasoft.epedidos.mappers.CustomerMapper;
import br.com.inovasoft.epedidos.models.dtos.CustomerDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.Customer;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@ApplicationScoped
public class CustomerService extends BaseService<Customer> {

    @Inject
    TokenService tokenService;

    @Inject
    CustomerMapper mapper;

    public PaginationDataResponse listAll(int page) {
        PanacheQuery<Customer> listCustomers = Customer.find(
                "select p from Customer p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Customer> dataList = listCustomers.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) Customer.count());
    }

    public List<CustomerDto> listActive() {

        List<Customer> dataList = Customer
                .find("select p from Customer p where p.systemId = ?1 and p.deletedOn is null order by p.name",
                        tokenService.getSystemId())
                .list();

        return mapper.toDto(dataList);
    }

    public PaginationDataResponse<CustomerDto> listCustomersBySystemKey(String systemKey, int page) {
        PanacheQuery<Customer> listCustomers = Customer.find(
                "select p from Customer p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<Customer> dataList = listCustomers.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage, (int) Customer.count());
    }

    public Customer findById(Long id) {
        return Customer.find("select p from Customer p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public CustomerDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public CustomerDto saveDto(CustomerDto dto) {
        Customer entity = mapper.toEntity(dto);

        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        return mapper.toDto(entity);
    }

    public CustomerDto update(Long id, CustomerDto dto) {
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