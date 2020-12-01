package br.com.inovasoft.epedidos.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.inovasoft.epedidos.mappers.AccountToPayMapper;
import br.com.inovasoft.epedidos.models.dtos.AccountToPayDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.AccountToPay;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@ApplicationScoped
public class AccountToPayService extends BaseService<AccountToPay> {

    @Inject
    TokenService tokenService;

    @Inject
    AccountToPayMapper mapper;

    public PaginationDataResponse listAll(int page) {
        PanacheQuery<AccountToPay> listAccountToPays = AccountToPay.find(
                "select p from AccountToPay p where p.systemId = ?1 and p.deletedOn is null",
                tokenService.getSystemId());

        List<AccountToPay> dataList = listAccountToPays.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) AccountToPay.count());
    }

    public List<AccountToPayDto> listActive() {

        List<AccountToPay> dataList = AccountToPay
                .find("select p from AccountToPay p where p.systemId = ?1 and p.deletedOn is null order by p.name",
                        tokenService.getSystemId())
                .list();

        return mapper.toDto(dataList);
    }

    public PaginationDataResponse listAccountToPaysBySystemKey(String systemKey, int page) {
        PanacheQuery<AccountToPay> listAccountToPays = AccountToPay.find(
                "select p from AccountToPay p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<AccountToPay> dataList = listAccountToPays.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) AccountToPay.count());
    }

    public AccountToPay findById(Long id) {
        return AccountToPay
                .find("select p from AccountToPay p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                        tokenService.getSystemId())
                .firstResult();
    }

    public AccountToPayDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public AccountToPayDto saveDto(AccountToPayDto dto) {
        AccountToPay entity = mapper.toEntity(dto);

        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        return mapper.toDto(entity);
    }

    public AccountToPayDto update(Long id, AccountToPayDto dto) {
        AccountToPay entity = findById(id);

        mapper.updateEntityFromDto(dto, entity);

        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        AccountToPay.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}