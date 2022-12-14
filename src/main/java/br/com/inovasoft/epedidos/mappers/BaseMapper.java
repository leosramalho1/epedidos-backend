package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.BaseEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;

public interface BaseMapper<E extends BaseEntity, D> {

    E toEntity(D dto);

    D toDto(E entity);

    List<D> toDto(List<E> entity);

    List<E> toEntity(List<D> dto);

    Set<D> toDto(Set<E> entity);

    Set<E> toEntity(Set<D> dto);
    
    E updateEntityFromDto(D source, @MappingTarget E target);

    D updateDtoFromEntity(E source, @MappingTarget D target);

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    E updateEntityIgnoringNull(E source, @MappingTarget E target);

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    D updateDtoIgnoringNull(D source, @MappingTarget D target);

}