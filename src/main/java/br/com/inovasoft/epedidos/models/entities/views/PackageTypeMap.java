package br.com.inovasoft.epedidos.models.entities.views;

import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackageTypeMap implements Serializable {

    private static final long serialVersionUID = -498163837314213415L;

    @JsonProperty(value = "tipoEmbalagem")
    private PackageTypeEnum packageType;
    private Integer totalComprado;

}
