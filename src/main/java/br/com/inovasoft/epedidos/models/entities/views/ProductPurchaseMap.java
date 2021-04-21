package br.com.inovasoft.epedidos.models.entities.views;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPurchaseMap implements Serializable {

    private static final long serialVersionUID = -498163837314213415L;
    
    private Long id;

}
