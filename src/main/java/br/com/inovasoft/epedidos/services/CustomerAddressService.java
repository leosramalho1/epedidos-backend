package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.CustomerAddressMapper;
import br.com.inovasoft.epedidos.models.dtos.CustomerAddressDto;
import br.com.inovasoft.epedidos.models.entities.Address;
import br.com.inovasoft.epedidos.models.entities.City;
import br.com.inovasoft.epedidos.models.entities.CustomerAddress;
import br.com.inovasoft.epedidos.security.TokenService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class CustomerAddressService extends BaseService<CustomerAddress> {

    @Inject
    TokenService tokenService;

    @Inject
    CustomerService customerService;

    @Inject
    CustomerAddressMapper mapper;

    public CustomerAddress findById(Long idCustomer, Long id) {
        return CustomerAddress
                .find("select ca from CustomerAddress ca, Customer c " + "where c.id = ca.customer.id and c.id = ?1"
                        + "and ca.id = ?2 and c.deletedOn is null and ca.deletedOn is null", idCustomer, id)
                .firstResult();
    }

    public List<CustomerAddressDto> findAddressesDtoById(Long customerId, Long systemId) {
        List<CustomerAddress> addressesByCustomerAndSystemId = findAddressesByCustomerAndSystemId(customerId, systemId);
        return mapper.toDto(addressesByCustomerAndSystemId);
    }

    public List<CustomerAddress> findAddressesByCustomerAndSystemId(Long customerId, Long systemId) {
        return CustomerAddress.list("select ca from Customer c, CustomerAddress ca "
                + "where c.id = ca.customer.id and c.id = ?1 and c.systemId = ?2 "
                + "and c.deletedOn is null and ca.deletedOn is null", customerId, systemId);
    }

    public CustomerAddress findAddressesByTokenAndId(Long id) {
        return CustomerAddress.find(
                "select ca from Customer c, CustomerAddress ca " + "where c.id = ca.customer.id and c.systemId = ?1 "
                        + "and ca.id = ?2 " + "and c.deletedOn is null and ca.deletedOn is null",
                tokenService.getSystemId(), id).firstResult();
    }


    public List<CustomerAddressDto> findAddressesByIdCustomer(Long idCustomer) {
        return mapper.toDto(CustomerAddress.list("select ca from CustomerAddress ca, Customer c "
                + "where ca.customer.id = c.id " + "and c.id = ?1 and ca.deletedOn is null", idCustomer));
    }

    public CustomerAddressDto saveDto(Long idCustomer, CustomerAddressDto dto) {
        CustomerAddress entity = mapper.toEntity(dto);

        entity.getAddress().setCity(null);

        mapper.updateEntityFromDto(dto, entity);

        entity.setCustomer(customerService.findById(idCustomer));

        Address address = entity.getAddress();
        address.setCity(City.find("id", dto.getAddress().getCity().getId()).firstResult());
        address.persist();

        super.save(entity);

        return mapper.toDto(entity);
    }

    public CustomerAddressDto update(Long id, CustomerAddressDto dto) {
        CustomerAddress entity = findById(dto.getCustomer().getId(), id);
        mapper.updateEntityFromDto(dto, entity);
        entity.persist();
        return mapper.toDto(entity);
    }


    public CustomerAddressDto updateDto(Long idCustomer, CustomerAddressDto dto) {
        CustomerAddress entity = findAddressesByTokenAndId(dto.getId());

        if (isDeliveryAddressChangedToTrue(dto, entity)
                || isPrimaryAddressChangedToTrue(dto, entity)) {
            findAddressesByCustomerAndSystemId(dto.getCustomer().getId(), tokenService.getSystemId()).stream()
                    .filter(i -> !i.getId().equals(entity.getId())).forEach(i -> {
                if (isDeliveryAddressChangedToTrue(dto, entity)) {
                    i.setDeliveryAddress(false);
                }
                if (isPrimaryAddressChangedToTrue(dto, entity)) {
                    i.setPrimaryAddress(false);
                }
                i.persist();
            });
        }

        entity.getAddress().setCity(null);

        mapper.updateEntityFromDto(dto, entity);

        Address address = entity.getAddress();
        address.setCity(City.find("id", dto.getAddress().getCity().getId()).firstResult());
        address.persist();

        entity.setCustomer(customerService.findById(idCustomer));
        super.save(entity);

        return mapper.toDto(entity);
    }

    private boolean isDeliveryAddressChangedToTrue(CustomerAddressDto dto, CustomerAddress entity) {
        return dto.isDeliveryAddress() && !entity.isDeliveryAddress();
    }

    private boolean isPrimaryAddressChangedToTrue(CustomerAddressDto dto, CustomerAddress entity) {
        return dto.isPrimaryAddress() && !entity.isPrimaryAddress();
    }


    public void softDelete(Long id, Long idCostumer) {
        CustomerAddress customerAddress = findById(idCostumer, id);
        if (customerAddress != null) {
            customerAddress.setDeletedOn(LocalDateTime.now());
            customerAddress.persist();
        }
    }
}