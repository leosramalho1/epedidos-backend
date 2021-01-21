package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.ArrayList;

@Data
@EqualsAndHashCode(callSuper = true)
public class FormDataDto extends ArrayList<FormDataDto> {

    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private InputStream data;

    @FormParam("filename")
    @PartType(MediaType.TEXT_PLAIN)
    private String fileName;

    @FormParam("mimetype")
    @PartType(MediaType.TEXT_PLAIN)
    private String mimeType;

    @FormParam("size")
    @PartType(MediaType.TEXT_PLAIN)
    private String size;

}