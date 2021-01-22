package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.exceptions.IllegalCustomerPayTypeException;
import br.com.inovasoft.epedidos.mappers.OrderItemMapper;
import br.com.inovasoft.epedidos.mappers.OrderMapper;
import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.dtos.OrderItemDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.models.entities.*;
import br.com.inovasoft.epedidos.models.enums.CustomerPayTypeEnum;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OrderService extends BaseService<Order> {

    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    @Inject
    TokenService tokenService;

    @Inject
    OrderMapper mapper;

    @Inject
    OrderItemMapper orderItemMapper;

    @Inject
    AccountToReceiveService accountToReceiveService;

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

    public PaginationDataResponse<OrderItemDto> listOrderItemByProductAndCustomer(Long idProduct, Long customerId) {

        PanacheQuery<OrderItem> listOrders = OrderItem.find(
                "select i from OrderItem i, Order o " +
                        "where o.id = i.order.id and i.product.id = ?1 and o.customer.id = ?2" +
                        "and o.systemId = ?3 and o.status = ?4",
                idProduct, customerId, tokenService.getSystemId(), OrderEnum.OPEN);

        List<OrderItem> dataList = listOrders.list();

        return new PaginationDataResponse<>(orderItemMapper.toDto(dataList), limitPerPage, (int) Order.count());
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

        order.setItens(orderItemMapper.toDto(OrderItem.list("order.id = ?1 order by product.name", order.getId())));

        return order;
    }

    @Transactional
    public OrderDto saveDto(OrderDto dto) {
        Order entity = mapper.toEntity(dto);
        entity.setCustomer(Customer.findById(dto.getIdCustomers().get(0)));
        entity.setSystemId(tokenService.getSystemId());
        entity.setTotalLiquidProducts(BigDecimal.ZERO);
        entity.setTotalProducts(0);
        entity.setCreatedOn(LocalDateTime.now());
        entity.setTotalValueProducts(BigDecimal.ZERO);
        entity.persist();

        List<OrderItem> itens = orderItemMapper.toEntity(dto.getItens());
        for (OrderItem item : itens) {
            item.setOrder(entity);
            if(item.getProduct() != null) {
                item.setWeidth(item.getProduct().getWeidth());
            }
            OrderItem.persist(item);
        }
  /* Conta a receber será gerado no fechamento e não quando o pedido é criado...um conta a receber poderá ter mais de um pedidos vinculado a ele
        AccountToReceive accountToReceive = accountToReceiveService
                .buildAccountToReceive(new AccountToReceive(), entity.getCustomer(),
                        calculateTotalValueProducts(itens, entity.getCustomer()));
        accountToReceive.persist();
        entity.setAccountToReceive(accountToReceive);
        entity.persist();*/

        return mapper.toDto(entity);
    }

    @Transactional
    public OrderDto saveDtoFromApp(OrderDto dto) {
        Order entity = mapper.toEntity(dto);
        entity.setCustomer(Customer.find("cpfCnpj=?1",tokenService.getJsonWebToken().getSubject()).firstResult());
        entity.setSystemId(tokenService.getSystemId());
        entity.setCreatedOn(LocalDateTime.now());
        entity.setTotalLiquidProducts(BigDecimal.ZERO);
        entity.setTotalProducts(0);
        entity.setTotalValueProducts(BigDecimal.ZERO);
        Order.persist(entity);

        List<OrderItem> itens = orderItemMapper.toEntity(dto.getItens());
        for (OrderItem item : itens) {
            item.setOrder(entity);
            if(item.getProduct() != null) {
                item.setWeidth(item.getProduct().getWeidth());
            }
            OrderItem.persist(item);
        }

        /* Conta a receber será gerado no fechamento e não quando o pedido é criado...um conta a receber poderá ter mais de um pedidos vinculado a ele
        AccountToReceive accountToReceive = Optional.ofNullable(entity.getAccountToReceive()).orElse(new AccountToReceive());
        accountToReceiveService.buildAccountToReceive(accountToReceive, entity.getCustomer(),
                calculateTotalValueProducts(itens, entity.getCustomer()));
        accountToReceive.persist();

        entity.setAccountToReceive(accountToReceive);
        entity.persist();*/
        return mapper.toDto(entity);
    }

    @Transactional
    public OrderDto update(Long id, OrderDto dto) {
        Order entity = Order.findById(id);

        OrderItem.delete("order.id=?1", id);
        if(dto.getItens() != null){
            List<OrderItem> itens = orderItemMapper.toEntity(dto.getItens());
            for (OrderItem item : itens) {
                item.setId(null);
                item.setOrder(entity);
                OrderItem.persist(item);
            }
        }

        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Order.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

	public List<OrderDto> listAllByCustomer() {
        PanacheQuery<Order> listOrders = Order.find(
            "select p from Order p where p.systemId = ?1 and p.customer.cpfCnpj=?2 and p.deletedOn is null order by p.id desc", tokenService.getSystemId(),tokenService.getJsonWebToken().getSubject());

		return mapper.toDto(listOrders.list());
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

            BigDecimal totalValueProductsRealized = calculateTotalValueProducts(orderItems, customer);

            Integer totalProductsRealized = orderItems.stream().map(OrderItem::getRealizedAmount)
                    .reduce(0, Integer::sum);

            order.setTotalProductsRealized(totalProductsRealized);
            order.setTotalValueProductsRealized(totalValueProductsRealized);
            order.setTotalLiquidProductsRealized(totalValueProductsRealized);
            order.setStatus(OrderEnum.FINISHED);
            order.persist();
        });

        return;
    }

    private BigDecimal calculateTotalValueProducts(List<OrderItem> orderItems, @NotNull Customer customer) {
        BigDecimal totalValueProductsRealized;

        if(customer.getPayType() == CustomerPayTypeEnum.V){
            totalValueProductsRealized = orderItems.stream()
                    .map(orderItem -> orderItem.getUnitValue().add(customer.getPayValue()).add(orderItem.getUnitShippingCost()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if(customer.getPayType() == CustomerPayTypeEnum.P){
            BigDecimal payValue = customer.getPayValue().divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
            totalValueProductsRealized = orderItems.stream()
                    .map(orderItem -> orderItem.getUnitValue().multiply(payValue).add(orderItem.getUnitShippingCost()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            throw new IllegalCustomerPayTypeException();
        }

        return totalValueProductsRealized;
    }
}
