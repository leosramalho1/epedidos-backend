package br.com.inovasoft.epedidos.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.inovasoft.epedidos.mappers.SupplierMapper;
import br.com.inovasoft.epedidos.models.dtos.SupplierDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.Supplier;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@ApplicationScoped
public class SupplierService extends BaseService<Supplier> {

    @Inject
    TokenService tokenService;

    @Inject
    SupplierMapper mapper;

    public PaginationDataResponse listAll(int page) {
        PanacheQuery<Supplier> listSuppliers = Supplier.find(
                "select p from Supplier p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Supplier> dataList = listSuppliers.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) Supplier.count());
    }

    public PaginationDataResponse listSuppliersBySystemKey(String systemKey, int page) {
        PanacheQuery<Supplier> listSuppliers = Supplier.find(
                "select p from Supplier p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<Supplier> dataList = listSuppliers.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) Supplier.count());
    }

    public Supplier findById(Long id) {
        return Supplier.find("select p from Supplier p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public SupplierDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public SupplierDto saveDto(SupplierDto dto) {
        Supplier entity = mapper.toEntity(dto);

        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        return mapper.toDto(entity);
    }

    public SupplierDto update(Long id, SupplierDto dto) {
        Supplier entity = findById(id);

        mapper.updateEntityFromDto(dto, entity);

        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        Supplier.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}