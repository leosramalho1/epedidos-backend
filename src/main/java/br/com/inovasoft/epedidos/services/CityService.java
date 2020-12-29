package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.CityMapper;
import br.com.inovasoft.epedidos.models.dtos.CityDto;
import br.com.inovasoft.epedidos.models.entities.City;
import io.quarkus.panache.common.Sort;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CityService extends BaseService<City> {

    @Inject
    CityMapper mapper;

    public List<CityDto> listAllDtoByState(Long idState) {
        return mapper.toDto(City.list("state.id = ?1", Sort.by("name"), idState));
    }

    public City findById(Long id) {
        return City.find("select c from City c where id = ?1 and deletedOn is null", id).firstResult();
    }

    public CityDto findDtoById(Long id) {
        return mapper.toDto(findById(id));
    }

    public CityDto saveDto(CityDto dto) {
        City entity = mapper.toEntity(dto);

        super.save(entity);

        return mapper.toDto(entity);
    }

    public CityDto update(Long id, CityDto dto) {
        City entity = findById(id);
        mapper.updateEntityFromDto(dto, entity);
        entity.persist();
        return mapper.toDto(entity);
    }

}