package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.SetupMapper;
import br.com.inovasoft.epedidos.models.dtos.SetupDto;
import br.com.inovasoft.epedidos.models.entities.Setup;
import br.com.inovasoft.epedidos.models.enums.SetupEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.SchedulerException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class SetupService extends BaseService<Setup> {

    @Inject
    TokenService tokenService;

    @Inject
    OrderService orderService;

    @Inject
    SetupMapper mapper;

    public Setup find() {
        return (Setup) Setup
                .find("systemId = ?1 and deletedOn is null", tokenService.getSystemId())
                .firstResult();
    }

    public List<Setup> listAll(List<SetupEnum> keys) {

        if(CollectionUtils.isNotEmpty(keys)) {
            return listByKeys(keys);
        }

        return list();
    }

    public List<Setup> list() {
        return Setup
                .list("systemId = ?1 and deletedOn is null",
                        tokenService.getSystemId());
    }

    public List<Setup> listByKeys(List<SetupEnum> keys) {
        return Setup
                .list("systemId = ?1 and deletedOn is null and key in (?2)",
                        tokenService.getSystemId(), keys);
    }

    public SetupDto findDtoByKey(SetupEnum key) {
        return mapper.toDto(findByKey(key));
    }

    public Setup findByKey(SetupEnum key) {
        return Setup
                .find("systemId = ?1 and deletedOn is null and key = ?2",
                        tokenService.getSystemId(), key)
                .firstResult();
    }

    public Optional<Setup> findById(Long id) {
        return Setup.find("id = ?1 and deletedOn is null", id).firstResultOptional();
    }

    public SetupDto findDto() {
        return mapper.toDto(find());
    }

    @Transactional
    public SetupDto saveDto(SetupDto dto) throws SchedulerException {
        Setup entity = mapper.toEntity(dto);
        entity.setSystemId(tokenService.getSystemId());

        if(dto.getKey() == SetupEnum.CRON_ORDER_TO_PURCHASE) {
            orderService.schedulerOrderToPurchase(dto.getValue(), tokenService.getSystemId());
        }

        entity.persist();
        return mapper.toDto(entity);
    }

    @Transactional
    public SetupDto update(SetupDto dto) throws SchedulerException {
        Setup entity = Setup.findById(dto.getId());
        mapper.updateEntityFromDto(dto, entity);
        entity.setSystemId(tokenService.getSystemId());

        if(entity.getKey() == SetupEnum.CRON_ORDER_TO_PURCHASE) {
            orderService.schedulerOrderToPurchase(dto.getValue(), tokenService.getSystemId());
        }

        entity.persist();
        return mapper.toDto(entity);
    }

}