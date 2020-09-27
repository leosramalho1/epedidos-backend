package br.com.inovasoft.epedidos.services;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.inovasoft.epedidos.mappers.CategoryMapper;
import br.com.inovasoft.epedidos.models.dtos.CategoryDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.references.Category;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@ApplicationScoped
public class CategoryService extends BaseService<Category> {

    @Inject
    TokenService tokenService;

    @Inject
    CategoryMapper mapper;

    public PaginationDataResponse listBySystemKey(String systemKey, int page) {
        PanacheQuery<Category> listProducts = Category.find(
                "select p from Category p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<Category> dataList = listProducts.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) Category.count(
                "from Category p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey));
    }

    public PaginationDataResponse listAll(Integer page) {
        Long systemId = tokenService.getSystemId();
        PanacheQuery<Category> list = Category.find("select c from Category c where c.systemId = ?1", systemId);
        List<Category> dataList;

        if (!Objects.isNull(page)) {
            dataList = list.page(Page.of(page - 1, limitPerPage)).list();
        } else {
            dataList = list.list();
            limitPerPage = dataList.size();
        }

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage,
                (int) Category.count("systemId = ?1", systemId));
    }

    public Category findById(Long id) {
        return Category.find("select c from Category c where id = ?1 and systemId = ?2 and deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public CategoryDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public CategoryDto saveDto(CategoryDto dto) {
        Long systemId = tokenService.getSystemId();
        Category entity = mapper.toEntity(dto);

        entity.setSystemId(systemId);

        super.save(entity);

        return mapper.toDto(entity);
    }

    public CategoryDto update(Long id, CategoryDto dto) {
        Category entity = findById(id);
        mapper.updateEntityFromDto(dto, entity);
        entity.persist();
        return mapper.toDto(entity);
    }

}