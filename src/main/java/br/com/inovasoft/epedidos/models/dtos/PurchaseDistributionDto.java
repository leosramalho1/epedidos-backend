package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseDistributionDto {

    private Long id;
    private Long idPurchase;
    private Long idOrder;
    private Long idProduct;
    private String nameProduct;
    private Integer quantity;
    private BigDecimal unitValue;
    private BigDecimal valueCharged;
    private BigDecimal totalValue;
    private BigDecimal unitShippingCost;
    private Long idCustomer;
    private PackageTypeEnum packageType;
    private String orderDate;
    private String distributionDate;

    public BigDecimal getTotalValue() {
        if(Objects.isNull(valueCharged) || Objects.isNull(quantity)) {
            return BigDecimal.ZERO;
        }
        return valueCharged.multiply(BigDecimal.valueOf(quantity));
    }
}