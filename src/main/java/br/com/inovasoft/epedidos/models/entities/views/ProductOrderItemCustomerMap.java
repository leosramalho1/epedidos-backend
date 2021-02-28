package br.com.inovasoft.epedidos.models.entities.views;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductOrderItemCustomerMap implements Serializable {

    private static final long serialVersionUID = 2933887673777124223L;
    
    private Long id;
    @JsonProperty(value = "cliente")
    private Long customer;
}
