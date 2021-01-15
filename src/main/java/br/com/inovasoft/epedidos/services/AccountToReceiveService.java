package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.AccountToReceiveMapper;
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

    @Inject
    public AccountToReceiveService(AccountToReceiveMapper mapper, TokenService tokenService) {
        super(mapper, tokenService);
    }

    @Override
    public PaginationDataResponse<AccountToReceiveDto> queryList(int page, String query, Parameters params) {
        PanacheQuery<AccountToReceive> list = AccountToReceive.find(query, Sort.by("dueDate").descending(), params);

        List<AccountToReceive> dataList = list.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage,
                (int) AccountToReceive.count(query, params));
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