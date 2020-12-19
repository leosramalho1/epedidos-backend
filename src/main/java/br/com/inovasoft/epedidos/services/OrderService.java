package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.OrderItemMapper;
import br.com.inovasoft.epedidos.mappers.OrderMapper;
import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.models.entities.Customer;
import br.com.inovasoft.epedidos.models.entities.Order;
import br.com.inovasoft.epedidos.models.entities.OrderItem;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrderService extends BaseService<Order> {

    @Inject
    TokenService tokenService;

    @Inject
    OrderMapper mapper;

    @Inject
    OrderItemMapper orderItemmapper;

    public PaginationDataResponse<OrderDto> listAll(int page) {
        PanacheQuery<Order> listOrders = Order.find(
                "select p from Order p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Order> dataList = listOrders.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage, (int) Order.count());
    }

    public PaginationDataResponse<OrderDto> listOrdersBySystemKey(String systemKey, int page) {
        PanacheQuery<Order> listOrders = Order.find(
                "select p from Order p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<Order> dataList = listOrders.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage, (int) Order.count());
    }

    public Order findById(Long id) {
        return Order.find("select p from Order p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public PurchaseDto getLastPurchaseByIdBuyer(Long buyerId) {

        // Group order bybuy and yesterday date

        // Decriase the purchase realized
        return null;
    }

    public OrderDto findDtoById(Long id) {
        Order entity = findById(id);

        OrderDto order = mapper.toDto(entity);
        order.setIdCustomers(new ArrayList<>());
        order.getIdCustomers().add(entity.getCustomer().getId());

        order.setItens(orderItemmapper.toDto(OrderItem.list("order.id = ?1 order by product.name", order.getId())));

        return order;
    }

    @Transactional
    public OrderDto saveDto(OrderDto dto) {
        Order entity = mapper.toEntity(dto);
        entity.setCustomer(Customer.findById(dto.getIdCustomers().get(0)));
        entity.setSystemId(tokenService.getSystemId());
        entity.setTotalLiquidProducts(BigDecimal.ZERO);
        entity.setTotalProducts(0);
        entity.setTotalValueProducts(BigDecimal.ZERO);
        super.save(entity);

        List<OrderItem> itens = orderItemmapper.toEntity(dto.getItens());
        for (OrderItem item : itens) {
            item.setOrder(entity);
            OrderItem.persist(item);
        }

        return mapper.toDto(entity);
    }

    @Transactional
    public OrderDto update(Long id, OrderDto dto) {
        Order entity = Order.findById(id);

        mapper.updateEntityFromDto(dto, entity);

        Order.persist(entity);

        OrderItem.delete("order.id=?1", id);
        List<OrderItem> itens = orderItemmapper.toEntity(dto.getItens());
        for (OrderItem item : itens) {
            item.setId(null);
            item.setOrder(entity);
            OrderItem.persist(item);
        }

        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Order.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}
