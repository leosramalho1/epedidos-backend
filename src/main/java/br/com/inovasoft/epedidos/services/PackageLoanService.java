package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.PackageLoanMapper;
import br.com.inovasoft.epedidos.models.dtos.PackageLoanDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.PackageLoan;
import br.com.inovasoft.epedidos.models.enums.ResponsibleTypeEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class PackageLoanService extends BaseService<PackageLoan> {

    @Inject
    EntityManager em;

    @Inject
    TokenService tokenService;

    @Inject
    PackageLoanMapper mapper;

    public PaginationDataResponse<PackageLoanDto> listAll(Integer page) {

        String query = "select p from PackageLoan p where p.systemId = ?1 and p.deletedOn is null";
        PanacheQuery<PackageLoan> list = PackageLoan.find(query, tokenService.getSystemId());

        List<PackageLoan> dataList = list.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage, (int) PackageLoan.count(query, tokenService.getSystemId()));
    }


    public List<PackageLoanDto> listHistoryDto(Long id) {

        List<PackageLoan> dataList = AuditReaderFactory.get(em)
                .createQuery()
                .forRevisionsOfEntity(PackageLoan.class, true, true)
                .add(AuditEntity.id().eq(id))
                .add(AuditEntity.property("systemId").eq(tokenService.getSystemId()))
                .getResultList();

        List<PackageLoanDto> list = mapper.toDto(dataList);
        list.sort(Comparator.comparing(PackageLoanDto::getUpdatedOn).reversed());
        
        return list;

    }

    public PaginationDataResponse<PackageLoanDto> listAllPending(Integer page, String responsibleName, String responsibleType) {
        String query = "from PackageLoan p where p.systemId = :systemId and p.deletedOn is null " +
                " and p.returnedAmount < p.borrowedAmount ";
        Parameters parameters = Parameters.with("systemId", tokenService.getSystemId());

        ResponsibleTypeEnum typeEnum = ResponsibleTypeEnum.fromValue(responsibleType);
        if(ResponsibleTypeEnum.CUSTOMER == typeEnum) {
            query += " and p.customer is not null and p.supplier is null";
            if(StringUtils.isNotBlank(responsibleName)) {
                query += " and lower(p.customer.name) like lower('%" + responsibleName + "%')";
            }
        } else if(ResponsibleTypeEnum.SUPPLIER == typeEnum) {
            query += " and p.customer is null and p.supplier is not null";
            if(StringUtils.isNotBlank(responsibleName)) {
                query += " and lower(p.supplier.name) like lower('%" + responsibleName + "%')";
            }
        }

        String order = " order by p.createdOn desc";

        List<PackageLoan> dataList = PackageLoan.find(query + order, parameters)
                .page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage, (int) PackageLoan.count(query, parameters));
    }

    public PackageLoan findById(Long id) {
        return PackageLoan.find("select p from PackageLoan p where id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public PackageLoanDto findDtoById(Long id) {
        PackageLoanDto packageLoanDto = mapper.toDto(findById(id));
        packageLoanDto.setHistory(listHistoryDto(id));
        return packageLoanDto;
    }

    public PackageLoanDto saveDto(PackageLoanDto dto) {
        Long systemId = tokenService.getSystemId();
        PackageLoan entity = mapper.toEntity(dto);

        entity.setSystemId(systemId);

        super.save(entity);

        return mapper.toDto(entity);
    }

    public PackageLoanDto update(Long id, PackageLoanDto dto) {
        PackageLoan entity = findById(id);
        mapper.updateEntityFromDto(dto, entity);
        entity.setUserChange(tokenService.getUserEmail());
        entity.persist();
        return mapper.toDto(entity);
    }

    public PackageLoanDto finalizeLoan(Long id) {
        PackageLoan entity = findById(id);
        entity.setUserChange(tokenService.getUserEmail());
        entity.setReturnedAmount(entity.getBorrowedAmount());
        entity.persist();
        return mapper.toDto(entity);
    }

    @Override
    public void softDelete(Long id) {
        PackageLoan entity = findById(id);
        entity.setDeletedOn(LocalDateTime.now());
        entity.persist();
    }
}