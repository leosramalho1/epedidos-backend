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

    protected static final int limitPerPage = 31;

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


    public PaginationDataResponse<D> listByStatus(int page, List<PayStatusEnum> statusEnum) {
        String query = "systemId = ?1 and status in (?2)";
        return queryList(page, query, tokenService.getSystemId(), statusEnum);
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