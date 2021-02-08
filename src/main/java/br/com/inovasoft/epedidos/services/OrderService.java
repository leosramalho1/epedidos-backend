package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.OrderItemMapper;
import br.com.inovasoft.epedidos.mappers.OrderMapper;
import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.*;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.apache.commons.collections.CollectionUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class OrderService extends BaseService<Order> {

    @Inject
    TokenService tokenService;

    @Inject
    OrderMapper mapper;

    @Inject
    OrderItemMapper orderItemMapper;

    public PaginationDataResponse<OrderDto> listAll(int page) {
        PanacheQuery<Order> listOrders = Order.find(
                "select p from Order p where p.systemId = ?1 and p.deletedOn is null", tokenService.getSystemId());

        List<Order> dataList = listOrders.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage, (int) Order.count());
    }


    public Order findById(Long id) {
        return Order.find("select p from Order p where p.id = ?1 " +
                        "and p.systemId = ?2 and p.deletedOn is null",
                id, tokenService.getSystemId()).firstResult();
    }

    public OrderDto findDtoById(Long id) {
        Order entity = findById(id);

        OrderDto order = mapper.toDto(entity);
        order.setIdCustomer(entity.getCustomer().getId());

        order.setItens(orderItemMapper.toDto(OrderItem.list("order.id = ?1 order by product.name", order.getId())));

        return order;
    }

    @Transactional
    public OrderDto saveDto(OrderDto dto) {
        Order entity = mapper.toEntity(dto);
        entity.setCustomer(Customer.findById(dto.getIdCustomer()));
        entity.setSystemId(tokenService.getSystemId());
        entity.setCreatedOn(LocalDateTime.now());
        entity.persist();

        List<OrderItem> orderItems = orderItemMapper.toEntity(dto.getItens());
        saveOrderItems(entity, orderItems);

        return mapper.toDto(entity);
    }

    @Transactional
    public OrderDto saveDtoFromApp(OrderDto dto) {
        String cpfCnpj = tokenService.getJsonWebToken().getSubject();
        Customer customer = Customer.find("cpfCnpj", cpfCnpj).firstResult();

        Order entity = mapper.toEntity(dto);
        entity.setCustomer(customer);
        entity.setSystemId(tokenService.getSystemId());
        entity.persist();

        List<OrderItem> orderItems = orderItemMapper.toEntity(dto.getItens());
        saveOrderItems(entity, orderItems);

        return mapper.toDto(entity);
    }


    @Transactional
    public OrderDto update(Long id, OrderDto dto) {
        Order entity = Order.findById(id);
        OrderItem.delete("order.id=?1", id);
        List<OrderItem> orderItems = orderItemMapper.toEntity(dto.getItens());
        saveOrderItems(entity, orderItems);
        mapper.updateEntityFromDto(dto, entity);
        entity.setStatus(dto.getStatus());
        entity.persist();
        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Order.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

	public List<OrderDto> listAllByCustomer() {
        PanacheQuery<Order> listOrders = Order.find(
            "select p from Order p where p.systemId = ?1 " +
                    "and p.customer.cpfCnpj=?2 " +
                    "and p.deletedOn is null " +
                    "order by p.id desc", tokenService.getSystemId(), tokenService.getJsonWebToken().getSubject());

		return mapper.toDto(listOrders.list());
	}

    public List<OrderDto> listAllByCustomerAndAccounttoReceive(Long idAccountToReceive) {
        PanacheQuery<Order> listOrders = Order.find(
            "select p from Order p, PurchaseDistribution d where d.orderItem.id = p.id and p.systemId = ?1 and p.customer.cpfCnpj=?2 and d.accountToReceive.id=?3 and p.deletedOn is null order by p.id desc", tokenService.getSystemId(),tokenService.getJsonWebToken().getSubject(),idAccountToReceive);

		return mapper.toDto(listOrders.list());
	}


    private void saveOrderItems(Order entity, List<OrderItem> orderItems) {
        if (CollectionUtils.isNotEmpty(orderItems)) {
            orderItems.stream()
                    .filter(o -> o.getQuantity() > 0)
                    .forEach(orderItem -> {
                        orderItem.setId(null);
                        orderItem.setOrder(entity);
                        if(Objects.isNull(orderItem.getProduct().getShippingCost())) {
                            orderItem.setProduct(Product.findById(orderItem.getProduct().getId()));
                        }
                        orderItem.setUnitShippingCost(orderItem.getProduct().getShippingCost());
                        OrderItem.persist(orderItem);
                    });

        }
    }

    public void scheduler(Long buyerId) {

        List<Order> orders = Order.list("status", OrderEnum.OPEN);

        orders.forEach(order -> {
            List<OrderItem> orderItems = OrderItem.list("order.id", order.getId());
            @NotNull Customer customer = order.getCustomer();
            orderItems.forEach(orderItem -> {
                PackageLoan packageLoan = new PackageLoan();
                packageLoan.setCustomer(customer);
                packageLoan.setSystemId(order.getSystemId());
                packageLoan.setUserChange("system");
                packageLoan.setOrderItem(orderItem);
                packageLoan.setRemainingAmount(orderItem.getQuantity().longValue());
                packageLoan.persist();
            });

            Integer totalProductsRealized = orderItems.stream()
                    .map(OrderItem::getRealizedAmount)
                    .reduce(0, Integer::sum);

            order.setStatus(OrderEnum.FINISHED);
            order.persist();
        });

        return;
    }

}
