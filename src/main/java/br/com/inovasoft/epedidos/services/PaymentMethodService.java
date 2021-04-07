package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.PaymentMethodMapper;
import br.com.inovasoft.epedidos.models.dtos.PaymentMethodDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.references.PaymentMethod;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class PaymentMethodService extends BaseService<PaymentMethod> {

    @Inject
    TokenService tokenService;

    @Inject
    PaymentMethodMapper mapper;

 
    public PaginationDataResponse<PaymentMethodDto> listAll(Integer page, StatusEnum status) {
        
        Parameters parameters = Parameters.with("systemId", tokenService.getSystemId());
        String query = "deletedOn is null and systemId = :systemId";

        if(status != null) {
            query += " and status = :status";
            parameters.and("status", status);
        }
        
        int limitPerPagePaymentMethod = BaseService.LIMIT_PER_PAGE;
        PanacheQuery<PaymentMethod> list = PaymentMethod.find(query, Sort.by("id"), parameters);
        List<PaymentMethod> dataList;

        if (!Objects.isNull(page)) {
            dataList = list.page(Page.of(page - 1, limitPerPagePaymentMethod)).list();
        } else {
            dataList = list.list();
            limitPerPagePaymentMethod = dataList.size();
        }

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPagePaymentMethod,
                (int) PaymentMethod.count(query, parameters));
    }

    public PaymentMethod findById(Long id) {
        return PaymentMethod.find("id = ?1 and systemId = ?2 and deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public PaymentMethodDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    @Transactional
    public PaymentMethodDto saveDto(PaymentMethodDto dto) {
        Long systemId = tokenService.getSystemId();
        PaymentMethod entity = mapper.toEntity(dto);

        entity.setSystemId(systemId);
        entity.setStatus(StatusEnum.ACTIVE);

        super.save(entity);

        return mapper.toDto(entity);
    }

    @Transactional
    public PaymentMethodDto update(Long id, PaymentMethodDto dto) {
        PaymentMethod entity = findById(id);
        mapper.updateEntityFromDto(dto, entity);
        entity.persist();
        return mapper.toDto(entity);
    }

	@Transactional
	public void softDelete(Long id) {
		PaymentMethod.update("set deletedOn = now() where id = ?1", id);
	}
}