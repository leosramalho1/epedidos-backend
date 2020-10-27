package br.com.inovasoft.epedidos.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.inovasoft.epedidos.mappers.OrderMapper;
import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.Order;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@ApplicationScoped
public class OrderService extends BaseService<Order> {

    @Inject
    TokenService tokenService;

    @Inject
    OrderMapper mapper;

    public PaginationDataResponse listAll(int page) {
        PanacheQuery<Order> listOrders = Order.find(
                "select p from Order p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Order> dataList = listOrders.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) Order.count());
    }

    public PaginationDataResponse listOrdersBySystemKey(String systemKey, int page) {
        PanacheQuery<Order> listOrders = Order.find(
                "select p from Order p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<Order> dataList = listOrders.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) Order.count());
    }

    public Order findById(Long id) {
        return Order.find("select p from Order p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public OrderDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public OrderDto saveDto(OrderDto dto) {
        Order entity = mapper.toEntity(dto);

        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        return mapper.toDto(entity);
    }

    public OrderDto update(Long id, OrderDto dto) {
        Order entity = findById(id);

        mapper.updateEntityFromDto(dto, entity);

        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        Order.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}