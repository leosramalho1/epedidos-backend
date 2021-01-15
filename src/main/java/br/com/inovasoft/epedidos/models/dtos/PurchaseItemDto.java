package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseItemDto {

    private Long idProduct;
    private String nameProduct;
    private Integer quantity;
    private BigDecimal unitValue;
    private BigDecimal totalValue;

    public PurchaseItemDto(Long idProduct, String nameProduct, Integer quantity) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
    }

    public BigDecimal getTotalValue() {
        if(Objects.isNull(totalValue) && Objects.nonNull(quantity)
                && Objects.nonNull(unitValue)) {
            return unitValue.multiply(BigDecimal.valueOf(quantity));
        }

        return totalValue;
    }
}