package br.com.inovasoft.epedidos.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.inovasoft.epedidos.mappers.AccountToReceiveMapper;
import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.AccountToReceive;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

@ApplicationScoped
public class AccountToReceiveService extends BaseService<AccountToReceive> {

    @Inject
    TokenService tokenService;

    @Inject
    AccountToReceiveMapper mapper;

    public PaginationDataResponse listAll(int page) {
        PanacheQuery<AccountToReceive> listAccountToReceives = AccountToReceive.find(
                "select p from AccountToReceive p where p.systemId = ?1 and p.deletedOn is null",
                tokenService.getSystemId());

        List<AccountToReceive> dataList = listAccountToReceives.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) AccountToReceive.count());
    }

    public List<AccountToReceiveDto> listActive() {

        List<AccountToReceive> dataList = AccountToReceive
                .find("select p from AccountToReceive p where p.systemId = ?1 and p.deletedOn is null order by p.name",
                        tokenService.getSystemId())
                .list();

        return mapper.toDto(dataList);
    }

    public PaginationDataResponse listAccountToReceivesBySystemKey(String systemKey, int page) {
        PanacheQuery<AccountToReceive> listAccountToReceives = AccountToReceive.find(
                "select p from AccountToReceive p, CompanySystem c where p.systemId = c.id and c.systemKey = ?1 and p.deletedOn is null",
                systemKey);

        List<AccountToReceive> dataList = listAccountToReceives.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse(mapper.toDto(dataList), limitPerPage, (int) AccountToReceive.count());
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

        entity.setSystemId(tokenService.getSystemId());

        super.save(entity);

        return mapper.toDto(entity);
    }

    public AccountToReceiveDto update(Long id, AccountToReceiveDto dto) {
        AccountToReceive entity = findById(id);

        mapper.updateEntityFromDto(dto, entity);

        entity.persist();

        return mapper.toDto(entity);
    }

    public void delete(Long id) {
        AccountToReceive.update("set deletedOn = now() where id = ?1 and systemId = ?2", id,
                tokenService.getSystemId());
    }

}