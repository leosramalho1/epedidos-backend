package br.com.inovasoft.epedidos.models.entities.views;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Optional;

@Data
@EqualsAndHashCode(exclude = { "changed" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductCustomerMap implements Comparable<ProductCustomerMap>, Serializable {

    private static final long serialVersionUID = -1233342758358021560L;
    
    private Long id;
    @JsonProperty(value = "nome")
    private String name;
    @JsonProperty(value = "totalPedido")
    private Integer totalQuantity;
    private boolean changed;
    @JsonProperty(value = "totalDistribuido")
    private Integer totalDistributed;

    @Override
    public int compareTo(ProductCustomerMap o) {
        if (o == null) {
            return -1;
        }

        return Comparator.comparing(ProductCustomerMap::getName).compare(this, o);
    }

    public Integer getTotalDistributed() {
        return Optional.ofNullable(totalDistributed).orElse(totalQuantity);
    }
}
