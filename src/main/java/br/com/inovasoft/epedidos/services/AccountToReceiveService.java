package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.AccountToReceiveMapper;
import br.com.inovasoft.epedidos.mappers.CustomerMapper;
import br.com.inovasoft.epedidos.mappers.OrderMapper;
import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.dtos.CustomerDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.AccountToReceive;
import br.com.inovasoft.epedidos.models.entities.Customer;
import br.com.inovasoft.epedidos.models.entities.Order;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@ApplicationScoped
public class AccountToReceiveService extends BillingService<AccountToReceive, AccountToReceiveDto> {

    CustomerMapper customerMapper;

    OrderMapper orderMapper;

    @Inject
    AccountToReceiveService(AccountToReceiveMapper mapper, TokenService tokenService, CustomerMapper customerMapper,
                        OrderMapper orderMapper) {
        super(mapper, tokenService);
        this.customerMapper = customerMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public PaginationDataResponse<AccountToReceiveDto> queryList(int page, String query, Parameters params) {
        PanacheQuery<AccountToReceive> list = AccountToReceive.find(query, Sort.by("dueDate").and("customer.name").descending(), params);

        List<AccountToReceive> dataList = list.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage,
                (int) AccountToReceive.count(query, params));
    }


    public PaginationDataResponse<CustomerDto> buildAllByCustomer(int page) {
        String query = "select order from Order order, Customer customer " +
                "where order.customer.id = customer.id and order.deletedOn is null and customer.deletedOn is null " +
                "and order.accountToReceive is null and order.systemId = ?1 and order.status = ?2";

        PanacheQuery<Order> list = Order.find(query, tokenService.getSystemId(), OrderEnum.FINISHED);

        List<Order> dataList = list.page(Page.of(page - 1, limitPerPage)).list();

        Map<Customer, List<Order>> ordersByCustomer = dataList.stream()
                .collect(Collectors.groupingBy(Order::getCustomer));

        List<CustomerDto> collect = ordersByCustomer.entrySet().stream()
                .map(i -> {
                    List<AccountToReceiveDto> accountToReceives = orderMapper.toAccountToReceiveDto(i.getValue());
                    CustomerDto customerDto = customerMapper.toDto(i.getKey());
                    return customerDto.toBuilder()
                            .accountToReceives(accountToReceives)
                            .build();
                }).collect(Collectors.toList());

        return new PaginationDataResponse<>(collect, limitPerPage, (int) Customer.count());
    }

    public AccountToReceive findById(Long id) {
        return AccountToReceive
                .find("select p from AccountToReceive p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null",
                        id, tokenService.getSystemId())
                .firstResult();
    }

    public AccountToReceiveDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public AccountToReceiveDto saveDto(AccountToReceiveDto dto) {
        AccountToReceive entity = mapper.toEntity(dto);
        entity.setCustomer(Customer.findById(dto.getCustomer().getId()));
        entity.setSystemId(tokenService.getSystemId());
        super.save(entity);

        return mapper.toDto(entity);
    }

    public AccountToReceiveDto update(Long id, AccountToReceiveDto dto) {
        AccountToReceive entity = findById(id);
        entity.setCustomer(Customer.findById(dto.getCustomer().getId()));
        mapper.updateEntityFromDto(dto, entity);

        entity.persist();

        return mapper.toDto(entity);
    }

    public AccountToReceiveDto receive(Long id, AccountToReceiveDto dto) {

        AccountToReceive entity = findById(id);
        mapper.updateEntityFromDto(dto, entity);
        entity.setCustomer(Customer.findById(dto.getCustomer().getId()));
        entity.setSystemId(tokenService.getSystemId());
        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        AccountToReceive.update("set deletedOn = now() where id = ?1 and systemId = ?2", id,
                tokenService.getSystemId());
    }

    public AccountToReceive buildAccountToReceive(AccountToReceive accountToReceive,
                                                  Customer customer, BigDecimal originalValue){

        accountToReceive.setSystemId(tokenService.getSystemId());
        accountToReceive.setCustomer(customer);
        accountToReceive.setOriginalValue(originalValue);
        return accountToReceive;
    }
}