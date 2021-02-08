package br.com.inovasoft.epedidos.models.entities.views;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPurchaseMap {

    private Long id;

}
