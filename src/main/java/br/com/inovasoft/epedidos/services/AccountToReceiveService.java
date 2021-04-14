package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.AccountToReceiveMapper;
import br.com.inovasoft.epedidos.mappers.CustomerMapper;
import br.com.inovasoft.epedidos.mappers.OrderMapper;
import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.AccountToReceive;
import br.com.inovasoft.epedidos.models.entities.Customer;
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

@NoArgsConstructor
@ApplicationScoped
public class AccountToReceiveService extends BillingService<AccountToReceive, AccountToReceiveDto> {

    CustomerMapper customerMapper;

    OrderMapper orderMapper;

    @Inject
    AccountToReceiveService(AccountToReceiveMapper mapper, TokenService tokenService, CustomerMapper customerMapper,
                        OrderMapper orderMapper) {
        super(mapper, tokenService, AccountToReceive.class);
        this.customerMapper = customerMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public PaginationDataResponse<AccountToReceiveDto> queryList(int page, String query, Parameters params) {
        PanacheQuery<AccountToReceive> list = AccountToReceive.find(query,
                Sort.by("dueDate").descending().and("customer.name").descending(),
                params);

        List<AccountToReceive> dataList = list.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), LIMIT_PER_PAGE,
                (int) AccountToReceive.count(query, params));
    }


    public AccountToReceiveDto saveDto(AccountToReceiveDto dto) {
        AccountToReceive entity = mapper.toEntity(dto);
        entity.setCustomer(Customer.findById(dto.getCustomer().getId()));
        entity.setSystemId(tokenService.getSystemId());
        super.save(entity);

        return mapper.toDto(entity);
    }

    public AccountToReceive findById(Long id) {
        return AccountToReceive
                .find("id = ?1 and systemId = ?2 and deletedOn is null", id,
                        tokenService.getSystemId())
                .firstResult();
    }

    public AccountToReceiveDto findDtoById(Long id) {
        AccountToReceiveDto accountToReceiveDto = mapper.toDto(findById(id));
        accountToReceiveDto.setHistory(listHistoryDto(id));
        return accountToReceiveDto;
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

	public List<AccountToReceiveDto> listBillings() {

        PanacheQuery<AccountToReceive> listAccountToReceive= AccountToReceive.find(
                "select p from AccountToReceive p where p.systemId = ?1 and p.customer.cpfCnpj=?2  and p.deletedOn is null order by p.id desc", tokenService.getSystemId(),tokenService.getJsonWebToken().getSubject());

        return mapper.toDto(listAccountToReceive.list());
	}


}