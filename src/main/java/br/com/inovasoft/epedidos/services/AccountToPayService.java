package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.AccountToPayMapper;
import br.com.inovasoft.epedidos.mappers.CustomerMapper;
import br.com.inovasoft.epedidos.mappers.OrderMapper;
import br.com.inovasoft.epedidos.models.dtos.AccountToPayDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.AccountToPay;
import br.com.inovasoft.epedidos.models.entities.Supplier;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@NoArgsConstructor
@ApplicationScoped
public class AccountToPayService extends BillingService<AccountToPay, AccountToPayDto> {

    CustomerMapper customerMapper;

    OrderMapper orderMapper;

    @Inject
    AccountToPayService(AccountToPayMapper mapper, TokenService tokenService, CustomerMapper customerMapper,
                        OrderMapper orderMapper) {
        super(mapper, tokenService, AccountToPay.class);
        this.customerMapper = customerMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public PaginationDataResponse<AccountToPayDto> queryList(int page, String query, Parameters params) {
        PanacheQuery<AccountToPay> list = AccountToPay.find(query, Sort.by("dueDate").descending().and("supplier.name"), params);

        List<AccountToPay> dataList = list.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), LIMIT_PER_PAGE,
                (int) AccountToPay.count(query, params));
    }
    
    public AccountToPay findById(Long id) {
        return AccountToPay
                .find("select p from AccountToPay p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                        tokenService.getSystemId())
                .firstResult();
    }

    public AccountToPayDto findDtoById(Long id) {
        AccountToPayDto accountToPayDto = mapper.toDto(findById(id));
        accountToPayDto.setHistory(listHistoryDto(id));
        return accountToPayDto;
    }

    public AccountToPayDto saveDto(AccountToPayDto dto) {
        AccountToPay entity = mapper.toEntity(dto);
        entity.setSupplier(Supplier.findById(dto.getSupplier().getId()));
        entity.setSystemId(tokenService.getSystemId());
        super.save(entity);

        return mapper.toDto(entity);
    }

    public AccountToPayDto update(Long id, AccountToPayDto dto) {
        AccountToPay entity = findById(id);
        entity.setSupplier(Supplier.findById(dto.getSupplier().getId()));

        mapper.updateEntityFromDto(dto, entity);
        entity.addPaidOutValue(dto.getPayValue());

        entity.persist();

        return mapper.toDto(entity);
    }

    public AccountToPayDto pay(Long id, AccountToPayDto dto) {

        AccountToPay entity = findById(id);
        mapper.updateEntityFromDto(dto, entity);
        entity.addPaidOutValue(dto.getPayValue());
        entity.setSupplier(Supplier.findById(dto.getSupplier().getId()));
        entity.setSystemId(tokenService.getSystemId());
        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        AccountToPay.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}