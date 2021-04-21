package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.SupplierMapper;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.SupplierDto;
import br.com.inovasoft.epedidos.models.entities.Supplier;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class SupplierService extends BaseService<Supplier> {

    @Inject
    TokenService tokenService;

    @Inject
    SupplierMapper mapper;

    public PaginationDataResponse<SupplierDto> listAll(int page) {
        PanacheQuery<Supplier> listSuppliers = Supplier.find(
                "select p from Supplier p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Supplier> dataList = listSuppliers.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), LIMIT_PER_PAGE, (int) Supplier.count());
    }

    public List<SupplierDto> getSuggestions(String query) {
        List<Supplier> dataList = Supplier.list(
                "systemId = ?1 and upper(name) like ?2 and status = ?3 and deletedOn is null",  Sort.by("name"),
                tokenService.getSystemId(), "%" + query.toUpperCase() + "%", StatusEnum.ACTIVE);

        return mapper.toDto(dataList);
    }

    public PaginationDataResponse<SupplierDto> listSuppliersBySystemKey(String systemKey, int page) {
        PanacheQuery<Supplier> listSuppliers = Supplier.find(
                "select p from Supplier p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<Supplier> dataList = listSuppliers.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), LIMIT_PER_PAGE, (int) Supplier.count());
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

        if (Supplier.count("cpfCnpj=?1 and deletedOn is null", dto.getCpfCnpj()) > 0)
        throw new WebApplicationException(Response.status(400)
                .entity("Atenção, já existe um fornecedor cadastrado com o CPF/CNPJ, favor informar outro.").build());


        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        return mapper.toDto(entity);
    }

    public SupplierDto update(Long id, SupplierDto dto) {
        Supplier entity = findById(id);

        if (Supplier.count("cpfCnpj=?1 and id !=?2 deletedOn is null", dto.getCpfCnpj(), id) > 0)
        throw new WebApplicationException(Response.status(400)
                .entity("Atenção, já existe um fornecedor cadastrado com o CPF/CNPJ, favor informar outro.").build());


        mapper.updateEntityFromDto(dto, entity);

        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        Supplier.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}