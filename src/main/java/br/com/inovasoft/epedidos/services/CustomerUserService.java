package br.com.inovasoft.epedidos.services;

import java.time.LocalDateTime;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.inovasoft.epedidos.mappers.CustomerUserMapper;
import br.com.inovasoft.epedidos.models.dtos.CustomerUserDto;
import br.com.inovasoft.epedidos.models.entities.CustomerUser;
import br.com.inovasoft.epedidos.security.TokenService;

@ApplicationScoped
public class CustomerUserService extends BaseService<CustomerUser> {

    @Inject
    TokenService tokenService;

    @Inject
    CustomerService customerService;

    @Inject
    CustomerUserMapper mapper;

    public List<CustomerUserDto> findUsersDtoById(Long customerId, Long systemId) {
        List<CustomerUserDto> listResult = mapper.toDto(CustomerUser.list(
                "customer.id = ?1 and customer.systemId = ?2 " + "and customer.deletedOn is null and deletedOn is null",
                customerId, systemId));
        // Clear password
        listResult.forEach(item -> {
            item.setPassword(null);
        });

        return listResult;
    }

    public CustomerUserDto saveDto(Long idCustomer, CustomerUserDto dto) {
        CustomerUser entity = mapper.toEntity(dto);

        mapper.updateEntityFromDto(dto, entity);

        entity.setCustomer(customerService.findById(idCustomer));

        super.save(entity);

        return mapper.toDto(entity);
    }

    public CustomerUserDto update(Long id, CustomerUserDto dto) {
        CustomerUser entity = CustomerUser.findById(dto.getId());
        String storeTempPass = entity.getPassword();
        mapper.updateEntityFromDto(dto, entity);

        if (dto.getPassword() == null) {
            entity.setPassword(storeTempPass);
        }

        entity.persist();
        return mapper.toDto(entity);
    }

    public void softDelete(Long id, Long idCustumer) {
        CustomerUser customerUser = CustomerUser.findById(id);
        if (customerUser != null) {
            customerUser.setDeletedOn(LocalDateTime.now());
            customerUser.persist();
        }
    }
}