package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.ProductMapper;
import br.com.inovasoft.epedidos.models.dtos.OrderItemDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.ProductDto;
import br.com.inovasoft.epedidos.models.dtos.UserPortalDto;
import br.com.inovasoft.epedidos.models.entities.Product;
import br.com.inovasoft.epedidos.models.entities.UserPortal;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService extends BaseService<Product> {

    @Inject
    TokenService tokenService;

    @Inject
    ProductMapper mapper;

    public PaginationDataResponse<ProductDto> listAll(int page) {
        PanacheQuery<Product> listProducts = Product.find(
                "select p from Product p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Product> dataList = listProducts.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), LIMIT_PER_PAGE, (int) Product.count());
    }

    public List<OrderItemDto> listProductsToGrid() {
        PanacheQuery<Product> listProducts = Product.find(
                "select p from Product p where p.systemId = ?1 and p.deletedOn is null order by p.name",
                tokenService.getSystemId());

        return listProducts.list().stream()
                .map(item -> new OrderItemDto(item.getId(), item.getName()))
                .collect(Collectors.toList());
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
            result.setBuyer(UserPortalDto.builder().id(buyer.getId()).name(buyer.getName()).build());
        }
        return result;
    }

    public ProductDto saveDto(ProductDto dto) {
        Product entity = mapper.toEntity(dto);
        if(dto.getBuyer() != null) {
            UserPortal userPortal = (UserPortal) UserPortal
                    .findByIdOptional(dto.getBuyer().getId())
                    .orElseThrow(() -> new WebApplicationException(Response.status(400)
                            .entity("Comprador inválido!").build()));
            entity.setBuyerId(userPortal.getId());
        }
        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        return mapper.toDto(entity);
    }

    public ProductDto update(Long id, ProductDto dto) {
        Product entity = findById(id);

        mapper.updateEntityFromDto(dto, entity);
        if(dto.getBuyer() != null) {
            UserPortal userPortal = (UserPortal) UserPortal
                    .findByIdOptional(dto.getBuyer().getId())
                    .orElseThrow(() -> new WebApplicationException(Response.status(400)
                            .entity("Comprador inválido!").build()));
            entity.setBuyerId(userPortal.getId());
        }

        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        Product.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}