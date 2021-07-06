package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
public class ProductReport extends BaseEntity implements Serializable {

    @Id
    private Long id;
    private String name;
    private BigDecimal weight;
    private Long quantity;
    private BigDecimal unitValue;

    public ProductReport(Long id, String name, BigDecimal weight, Long quantity, BigDecimal unitValue) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.quantity = quantity;
        this.unitValue = unitValue;
    }

    public ProductReport() {
    }
}