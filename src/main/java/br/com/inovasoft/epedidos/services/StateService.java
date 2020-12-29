package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.StateMapper;
import br.com.inovasoft.epedidos.models.dtos.StateDto;
import br.com.inovasoft.epedidos.models.entities.State;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class StateService extends BaseService<State> {

    @Inject
    TokenService tokenService;

    @Inject
    StateMapper mapper;

    public List<StateDto> listAllDto() {
        return mapper.toDto(State.listAll(Sort.by("name")));
    }

    public State findById(Long id) {
        return State.find("select c from State c where id = ?1 and systemId = ?2 and deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public StateDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public StateDto saveDto(StateDto dto) {
        State entity = mapper.toEntity(dto);

        super.save(entity);

        return mapper.toDto(entity);
    }

    public StateDto update(Long id, StateDto dto) {
        State entity = findById(id);
        mapper.updateEntityFromDto(dto, entity);
        entity.persist();
        return mapper.toDto(entity);
    }

}