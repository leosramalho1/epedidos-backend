package br.com.inovasoft.epedidos.models.entities.views;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Comparator;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductCustomerMap implements Comparable<ProductCustomerMap> {

    private Long id;
    @JsonProperty(value = "nome")
    private String name;
    @JsonProperty(value = "totalPedido")
    private Integer totalQuantity;
    private boolean changed;

    @Override
    public int compareTo(ProductCustomerMap o) {
        if(o == null) {
            return -1;
        }

        return Comparator.comparing(ProductCustomerMap::getName).compare(this, o);
    }
}
