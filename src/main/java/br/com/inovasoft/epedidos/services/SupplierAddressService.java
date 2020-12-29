package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.SupplierAddressMapper;
import br.com.inovasoft.epedidos.models.dtos.SupplierAddressDto;
import br.com.inovasoft.epedidos.models.entities.Address;
import br.com.inovasoft.epedidos.models.entities.City;
import br.com.inovasoft.epedidos.models.entities.SupplierAddress;
import br.com.inovasoft.epedidos.security.TokenService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SupplierAddressService extends BaseService<SupplierAddress> {

    @Inject
    TokenService tokenService;

    @Inject
    SupplierService supplierService;

    @Inject
    SupplierAddressMapper mapper;

    public SupplierAddress findById(Long supplierId, Long id) {
        return SupplierAddress
                .find("select sa from SupplierAddress sa, Supplier s " + "where s.id = sa.supplier.id and s.id = ?1"
                        + "and sa.id = ?2 and s.deletedOn is null and sa.deletedOn is null", supplierId, id)
                .firstResult();
    }

    public List<SupplierAddressDto> findAddressesDtoById(Long supplierId, Long systemId) {
        return mapper.toDto(findAddressesBySupplierAndSystemId(supplierId, systemId));
    }

    public List<SupplierAddress> findAddressesBySupplierAndSystemId(Long supplierId, Long systemId) {
        return SupplierAddress.list("select sa from Supplier s, SupplierAddress sa "
                + "where s.id = sa.supplier.id and s.id = ?1 and s.systemId = ?2 "
                + "and s.deletedOn is null and sa.deletedOn is null", supplierId, systemId);
    }

    public SupplierAddress findAddressesByTokenAndId(Long id) {
        return SupplierAddress.find(
                "select sa from Supplier s, SupplierAddress sa where s.id = sa.supplier.id and s.systemId = ?1 "
                        + "and sa.id = ?2 and s.deletedOn is null and sa.deletedOn is null",
                tokenService.getSystemId(), id).firstResult();
    }


    public List<SupplierAddressDto> findAddressesByIdSupplier(Long supplierId) {
        return mapper.toDto(SupplierAddress.list("select sa from SupplierAddress sa, Supplier s "
                + "where sa.supplier.id = s.id and s.id = ?1 and sa.deletedOn is null", supplierId));
    }

    public SupplierAddressDto saveDto(Long idSupplier, SupplierAddressDto dto) {
        SupplierAddress entity = mapper.toEntity(dto);

        entity.getAddress().setCity(null);

        mapper.updateEntityFromDto(dto, entity);

        entity.setSupplier(supplierService.findById(idSupplier));

        Address address = entity.getAddress();
        address.setCity(City.find("id", dto.getAddress().getCity().getId()).firstResult());
        address.persist();

        super.save(entity);

        return mapper.toDto(entity);
    }

    public SupplierAddressDto update(Long id, SupplierAddressDto dto) {
        SupplierAddress entity = findById(dto.getSupplier().getId(), id);
        mapper.updateEntityFromDto(dto, entity);
        entity.persist();
        return mapper.toDto(entity);
    }


    public SupplierAddressDto updateDto(SupplierAddressDto dto) {
        SupplierAddress entity = findAddressesByTokenAndId(dto.getId());

        if (isDeliveryAddressChangedToTrue(dto, entity)
                || isPrimaryAddressChangedToTrue(dto, entity)) {
            findAddressesBySupplierAndSystemId(dto.getSupplier().getId(), tokenService.getSystemId()).stream()
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

        super.save(entity);

        return mapper.toDto(entity);
    }

    private boolean isDeliveryAddressChangedToTrue(SupplierAddressDto dto, SupplierAddress entity) {
        return dto.isDeliveryAddress() && !entity.isDeliveryAddress();
    }

    private boolean isPrimaryAddressChangedToTrue(SupplierAddressDto dto, SupplierAddress entity) {
        return dto.isPrimaryAddress() && !entity.isPrimaryAddress();
    }


    public void softDelete(Long id, Long idSupplier) {
        SupplierAddress supplierAddress = findById(idSupplier, id);
        if (supplierAddress != null) {
            supplierAddress.setDeletedOn(LocalDateTime.now());
            supplierAddress.persist();
        }
    }
}