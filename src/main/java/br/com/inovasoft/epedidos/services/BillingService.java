package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.BaseMapper;
import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.AccountToPay;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
public abstract class BillingService<T extends BaseEntity, D> extends BaseService<T> {

    TokenService tokenService;

    BaseMapper<T, D> mapper;

    public BillingService(BaseMapper<T, D> mapper, TokenService tokenService) {
        this.mapper = mapper;
        this.tokenService = tokenService;
    }

    public abstract PaginationDataResponse<D> queryList(int page, String query, Parameters params);

    public PaginationDataResponse<D> queryList(int page, String query, Object... params) {
        PanacheQuery<T> list = T.find(query, params);

        List<T> dataList = list.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage,
                (int) T.count(query, params));
    }

    public PaginationDataResponse<D> listAll(int page, List<PayStatusEnum> status, Long supplier, Long customer,
                                                          LocalDate dateMin, LocalDate dateMax) {
        Parameters parameters = Parameters.with("systemId", 1L);
        String query = "systemId = :systemId";

        if(supplier != null) {
            query += " and supplier.id = :supplier";
            parameters.and("supplier", supplier);
        }
        if(customer != null) {
            query += " and customer.id = :customer";
            parameters.and("customer", customer);
        }

        if(dateMin != null) {
            query += " and dueDate >= :dateMin";
            parameters.and("dateMin", dateMin);
        }

        if(dateMax != null) {
            query += " and dueDate <= :dateMax";
            parameters.and("dateMax", dateMax);
        }

        if(CollectionUtils.isNotEmpty(status)) {
            query += " and status in (:status)";
            parameters.and("status", status);
        }

        return queryList(page, query, parameters);

    }


    public PaginationDataResponse<D> listByStatus(int page, List<PayStatusEnum> statusEnum) {
        String query = "systemId = ?1 and status in (?2)";
        return queryList(page, query, tokenService.getSystemId(), statusEnum);
    }

    public PaginationDataResponse<D> listActive(int page) {
        String query = "systemId = ?1 and deletedOn is null " +
                "and payValue < originalValue or payDate is null";
        return queryList(page, query, tokenService.getSystemId());
    }

    public PaginationDataResponse<D> listInactive(int page) {
        String query = "systemId = ?1 and deletedOn is null " +
                "and payValue >= originalValue and payDate is not null";
        return queryList(page, query, tokenService.getSystemId());
    }

    public T findById(Long id) {
        return AccountToPay
                .find("id = ?1 and systemId = ?2 and deletedOn is null", id,
                        tokenService.getSystemId())
                .firstResult();
    }

    public D findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }


}