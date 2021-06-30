package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.OrderItemMapper;
import br.com.inovasoft.epedidos.mappers.OrderMapper;
import br.com.inovasoft.epedidos.models.dtos.CustomerBillingDto;
import br.com.inovasoft.epedidos.models.dtos.OrderDto;
import br.com.inovasoft.epedidos.models.dtos.OrderItemDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.Customer;
import br.com.inovasoft.epedidos.models.entities.Order;
import br.com.inovasoft.epedidos.models.entities.OrderItem;
import br.com.inovasoft.epedidos.models.entities.Product;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import com.cronutils.mapper.CronMapper;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class OrderService extends BaseService<Order> {

    @Inject
    TokenService tokenService;

    @Inject
    OrderMapper mapper;

    @Inject
    OrderItemMapper orderItemMapper;

    @Inject
    Scheduler quartz;

    @Inject
    PackageLoanService packageLoanService;

    @Inject
    ProductService productService;

    final CronParser unixParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX));

    public PaginationDataResponse<OrderDto> listAll(int page, List<OrderEnum> orderEnums) {

        if (CollectionUtils.isEmpty(orderEnums)) {
            orderEnums = List.of(OrderEnum.values());
        }

        String query = "deletedOn is null and systemId = ?1 and status in (?2)";
        PanacheQuery<Order> listOrders = Order.find(query, Sort.by("id").descending(), tokenService.getSystemId(),
                orderEnums);

        List<Order> dataList = listOrders.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), LIMIT_PER_PAGE,
                (int) Order.count(query, tokenService.getSystemId(), orderEnums));
    }

    public Order findById(Long id) {
        return Order.find("select p from Order p where p.id = ?1 " + "and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public OrderDto findDtoById(Long id) {
        Order entity = findById(id);

        OrderDto order = mapper.toDto(entity);
        order.setIdCustomer(entity.getCustomer().getId());
        List<OrderItem> listOrdem = OrderItem.list("order.id = ?1 order by product.name", order.getId());
        if (listOrdem != null && !listOrdem.isEmpty())
            order.setItens(orderItemMapper.toDto(listOrdem));

        return order;
    }

    public OrderDto findDtoByIdApp(Long id) {
        Order entity = findById(id);

        OrderDto order = mapper.toDto(entity);
        order.setIdCustomer(entity.getCustomer().getId());

        List<OrderItemDto> orderItemGlobal = productService.listProductsToGrid();
        order.setItens(orderItemGlobal);

        List<OrderItem> listOrdem = OrderItem.list("order.id = ?1 order by product.name", order.getId());
        if (listOrdem != null && !listOrdem.isEmpty()) {
            List<OrderItemDto> existingOrderItem = orderItemMapper.toDto(listOrdem);

            order.getItens().forEach(item -> {
                Optional<OrderItemDto> itemOptional = existingOrderItem.stream()
                        .filter(itemExisting -> itemExisting.getIdProduct() == item.getIdProduct()).findFirst();
                itemOptional.ifPresent(itemOptionalExisting -> {
                    item.setQuantity(itemOptionalExisting.getQuantity());
                });
            });
        }

        return order;
    }

    public void prepareOrderItemByStatusOrderApp(OrderEnum status, List<OrderItemDto> orderItemGlobal) {
        String cpfCnpj = tokenService.getJsonWebToken().getSubject();
        Customer customer = Customer.find("cpfCnpj", cpfCnpj).firstResult();

        List<OrderItem> listOrdem = OrderItem.list("order.customer.id = ?1 and order.status = ?2 order by product.name",
                customer.getId(), status);

        if (listOrdem != null && !listOrdem.isEmpty()) {
            List<OrderItemDto> existingOrderItem = orderItemMapper.toDto(listOrdem);

            orderItemGlobal.forEach(item -> {
                Optional<OrderItemDto> itemOptional = existingOrderItem.stream()
                        .filter(itemExisting -> itemExisting.getIdProduct() == item.getIdProduct()).findFirst();
                itemOptional.ifPresent(itemOptionalExisting -> {
                    item.setQuantity(itemOptionalExisting.getQuantity());
                });
            });
        }
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
        OrderItem.delete("order.id = ?1", id);
        List<OrderItem> orderItems = orderItemMapper.toEntity(dto.getItens());
        saveOrderItems(entity, orderItems);
        LocalDateTime createdOn = entity.getCreatedOn();
        mapper.updateEntityFromDto(dto, entity);
        entity.setCreatedOn(createdOn);
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
                "select p from Order p where p.systemId = ?1 " + "and p.customer.cpfCnpj=?2 "
                        + "and p.deletedOn is null " + "order by p.id desc",
                tokenService.getSystemId(), tokenService.getJsonWebToken().getSubject());

        return mapper.toDto(listOrders.list());
    }

    public List<OrderDto> listAllByCustomerAndAccounttoReceive(Long idAccountToReceive) {
        PanacheQuery<Order> listOrders = Order.find(
                "select p from Order p, PurchaseDistribution d where d.orderItem.id = p.id and p.systemId = ?1 and p.customer.cpfCnpj=?2 and d.accountToReceive.id=?3 and p.deletedOn is null order by p.id desc",
                tokenService.getSystemId(), tokenService.getJsonWebToken().getSubject(), idAccountToReceive);

        return mapper.toDto(listOrders.list());
    }

    private void saveOrderItems(Order entity, List<OrderItem> orderItems) {
        if (CollectionUtils.isNotEmpty(orderItems)) {
            orderItems.stream().filter(o -> o.getQuantity() > 0).forEach(orderItem -> {
                orderItem.setId(null);
                orderItem.setOrder(entity);
                if (Objects.isNull(orderItem.getProduct().getShippingCost())) {
                    orderItem.setProduct(Product.findById(orderItem.getProduct().getId()));
                }
                orderItem.setUnitShippingCost(orderItem.getProduct().getShippingCost());
                OrderItem.persist(orderItem);
            });

        }
    }

    @Transactional
    public int changeOrdersToStatusPurchase(Long systemId) {
        return Order.update("set status = ?1 where status = ?2 and systemId = ?3", OrderEnum.PURCHASE, OrderEnum.OPEN,
                systemId);
    }

    @Transactional
    public OrderDto changeOrdersToStatusPurchase() {
        int updates = changeOrdersToStatusPurchase(tokenService.getSystemId());
        return OrderDto.builder().updates(updates).build();
    }

    @Transactional
    public void closeOrders(List<CustomerBillingDto> customerBillingDtos) {

        customerBillingDtos.stream().map(CustomerBillingDto::getPurchaseDistributions).flatMap(Collection::stream)
                .forEach(purchaseDistributionDto -> {
                    Order order = Order.findById(purchaseDistributionDto.getIdOrder());
                    packageLoanService.registryPackageLoan(order.getOrderItems());
                    order.setStatus(OrderEnum.FINISHED);
                    order.persist();
                });

    }

    public void schedulerOrderToPurchase(@NotNull String cron, @NotNull Long systemId) throws SchedulerException {
        String cronQuartz = cronUnixToCronQuartz(cron);

        JobKey jobKey = JobKey.jobKey("order-job-" + systemId, "system-" + systemId);

        JobDetail job = JobBuilder.newJob(OrderToPurchaseJob.class).withIdentity(jobKey).build();

        job.getJobDataMap().put("systemId", systemId);

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("order-trigger-" + systemId, "system-" + systemId)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronQuartz)).startNow().build();

        if (quartz.checkExists(jobKey)) {
            quartz.deleteJob(jobKey);
        }

        quartz.scheduleJob(job, trigger);
    }

    private String cronUnixToCronQuartz(String cron) {
        Cron parse = unixParser.parse(cron);
        return CronMapper.fromUnixToQuartz().map(parse).asString();
    }

    public static class OrderToPurchaseJob implements Job {

        @Inject
        OrderService orderService;

        public void execute(JobExecutionContext context) {
            JobDetail jobDetail = context.getJobDetail();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            Long systemId = jobDataMap.getLong("systemId");

            log.info(" #### JOB #### - INICIO - Sistema: {}, Nome: {}, Próxima Execução: {}", systemId,
                    jobDetail.getKey(), context.getNextFireTime());
            int update = orderService.changeOrdersToStatusPurchase(systemId);
            log.info(" #### JOB #### - FIM - Sistema: {}, Nome: {}, Registros atualizados: {}", systemId,
                    jobDetail.getKey(), update);
        }

    }

}
