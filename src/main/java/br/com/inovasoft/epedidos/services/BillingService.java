package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.BaseMapper;
import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.dtos.BillingDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.panache.common.Parameters;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor
public abstract class BillingService<T extends BaseEntity, D extends BillingDto> extends BaseService<T> {

    @Inject
    EntityManager em;

    TokenService tokenService;

    BaseMapper<T, D> mapper;

    private Class<T> entityType;

    protected static final int LIMIT_PER_PAGE = 31;

    protected BillingService(BaseMapper<T, D> mapper, TokenService tokenService, Class<T> entityType) {
        this.mapper = mapper;
        this.tokenService = tokenService;
        this.entityType = entityType;
    }

    public abstract PaginationDataResponse<D> queryList(int page, String query, Parameters params);

    public PaginationDataResponse<D> listAll(int page, List<PayStatusEnum> status, Long supplier, Long customer,
                                             LocalDate dueDateMin, LocalDate dueDateMax,
                                             LocalDate paidOutDateMin, LocalDate paidOutDateMax) {
        Parameters parameters = Parameters.with("systemId", tokenService.getSystemId());
        String query = "systemId = :systemId";

        if(supplier != null) {
            query += " and supplier.id = :supplier";
            parameters.and("supplier", supplier);
        }

        if(customer != null) {
            query += " and customer.id = :customer";
            parameters.and("customer", customer);
        }

        if(dueDateMin != null) {
            query += " and dueDate >= :dueDateMin";
            parameters.and("dueDateMin", dueDateMin);
        }

        if(dueDateMax != null) {
            query += " and dueDate <= :dueDateMax";
            parameters.and("dueDateMax", dueDateMax);
        }

        if(paidOutDateMin != null) {
            query += " and paidOutDate >= :paidOutDateMin";
            parameters.and("paidOutDateMin", paidOutDateMin);
        }

        if(paidOutDateMax != null) {
            query += " and paidOutDate <= :paidOutDateMax";
            parameters.and("paidOutDateMax", paidOutDateMax);
        }

        if(CollectionUtils.isNotEmpty(status)) {
            query += " and status in (:status)";
            parameters.and("status", status);
        }

        return queryList(page, query, parameters);

    }

    public List<D> listHistoryDto(Long id) {

        List<T> dataList = AuditReaderFactory.get(em)
                .createQuery()
                .forRevisionsOfEntity(entityType, true, true)
                .add(AuditEntity.id().eq(id))
                .add(AuditEntity.property("systemId").eq(tokenService.getSystemId()))
                .getResultList();

        dataList.sort(Comparator.comparing(BaseEntity::getUpdatedOn).reversed());

        return mapper.toDto(dataList);

    }

    public abstract T findById(Long id);

}