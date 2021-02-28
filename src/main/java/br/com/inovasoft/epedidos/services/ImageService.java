package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.ImageMapper;
import br.com.inovasoft.epedidos.models.dtos.FormDataDto;
import br.com.inovasoft.epedidos.models.dtos.ImageDto;
import br.com.inovasoft.epedidos.models.entities.CompanySystem;
import br.com.inovasoft.epedidos.models.entities.Image;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.services.s3.S3Service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.StringJoiner;
import java.util.UUID;

@ApplicationScoped
public class ImageService extends BaseService<Image> {

    @Inject
    TokenService tokenService;

    @Inject
    S3Service s3Service;

    @Inject
    ImageMapper imageMapper;

    public Image findById(Long id) {
        return Image.find("select i from Image p where i.id = ?1 and i.system.id = ?2", id, tokenService.getSystemId())
                .firstResult();
    }

    public ImageDto saveFormData(FormDataDto formDataDto) {

        CompanySystem companySystem = CompanySystem.find("id", tokenService.getSystemId()).firstResult();

        StringJoiner stringJoiner = new StringJoiner("/");
        stringJoiner.add(companySystem.getCompany().getCnpj());
        stringJoiner.add(UUID.randomUUID().toString());
        String key = stringJoiner.toString();

        String url = s3Service.uploadFile(formDataDto.getData(), key, formDataDto.getMimeType(),
                Long.parseLong(formDataDto.getSize()));

        Image image = new Image();

        image.setSystem(companySystem);
        image.setKey(key);
        image.setUrl(url);
        image.setName(formDataDto.getFileName());
        image.persist();

        return imageMapper.toDto(image);
    }

}