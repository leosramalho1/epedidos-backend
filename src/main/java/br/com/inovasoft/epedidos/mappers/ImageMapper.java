package br.com.inovasoft.epedidos.mappers;

import br.com.inovasoft.epedidos.models.dtos.ImageDto;
import br.com.inovasoft.epedidos.models.entities.Image;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface ImageMapper extends BaseMapper<Image, ImageDto> {


}