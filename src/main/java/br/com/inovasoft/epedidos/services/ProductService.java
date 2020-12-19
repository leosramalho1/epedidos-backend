package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.ProductMapper;
import br.com.inovasoft.epedidos.models.dtos.OrderItemDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.ProductDto;
import br.com.inovasoft.epedidos.models.entities.Product;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.util.SuggestionUtil;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService extends BaseService<Product> {

    @Inject
    TokenService tokenService;

    @Inject
    ProductMapper mapper;

    public PaginationDataResponse listAll(int page) {
        PanacheQuery<Product> listProducts = Product.find(
                "select p from Product p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Product> dataList = listProducts.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) Product.count());
    }

    public List<OrderItemDto> listProductsToGrid() {
        PanacheQuery<Product> listProducts = Product.find(
                "select p from Product p where p.systemId = ?1 and p.deletedOn is null order by p.name",
                tokenService.getSystemId());

        return listProducts.list().stream().map(item -> new OrderItemDto(item.getId(), item.getName(), BigDecimal.ONE))
                .collect(Collectors.toList());
    }

    public PaginationDataResponse listProductsBySystemKey(String systemKey, int page) {
        PanacheQuery<Product> listProducts = Product.find(
                "select p from Product p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<Product> dataList = listProducts.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) Product.count());
    }

    public ProductDto findProductsBySystemKeyAndId(String systemKey, Long id) {
        Product product = Product.find(
                "select p from Product p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.id =?2 and p.deletedOn is null",
                systemKey, id).firstResult();

        return mapper.toDto(product);
    }

    public Product findById(Long id) {
        return Product.find("select p from Product p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public ProductDto findDtoById(Long id) {
        Product entity = findById(id);
        ProductDto result = mapper.toDto(entity);
        if (entity.getBuyerId() != null) {
            UserPortal buyer = UserPortal.findById(entity.getBuyerId());
            result.setBuyerCodeName(SuggestionUtil.build(buyer.getId(), buyer.getName()));
        }
        return result;
    }

    public ProductDto saveDto(ProductDto dto) {
        Product entity = mapper.toEntity(dto);
        if (StringUtils.isNotBlank(dto.getBuyerCodeName()))
            entity.setBuyerId(SuggestionUtil.extractId(dto.getBuyerCodeName()));
        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        return mapper.toDto(entity);
    }

    public ProductDto update(Long id, ProductDto dto) {
        Product entity = findById(id);

        mapper.updateEntityFromDto(dto, entity);
        if (StringUtils.isNotBlank(dto.getBuyerCodeName()))
            entity.setBuyerId(SuggestionUtil.extractId(dto.getBuyerCodeName()));

        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        Product.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}