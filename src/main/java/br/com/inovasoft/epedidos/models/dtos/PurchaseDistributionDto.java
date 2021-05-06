package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.configuration.AppConstants;
import br.com.inovasoft.epedidos.models.enums.CustomerPayTypeEnum;
import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private BigDecimal unitCustomerCost;
    private Long idCustomer;
    private PackageTypeEnum packageType;
    private String orderDate;
    private String distributionDate;
    private BigDecimal totalShippingCost;
    private BigDecimal totalCustomerCost;
    private CustomerPayTypeEnum customerPayType;

    public BigDecimal getTotalValue() {
        if(Objects.isNull(valueCharged) || Objects.isNull(quantity)) {
            return BigDecimal.ZERO;
        }
        return valueCharged.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getTotalShippingCost() {

        if (Objects.isNull(unitShippingCost) || Objects.isNull(quantity)) {
            return BigDecimal.ZERO;
        } else if (Objects.isNull(totalShippingCost)) {
            totalShippingCost = unitShippingCost.multiply(BigDecimal.valueOf(quantity));
        }

        return totalShippingCost;
    }
    public BigDecimal getTotalCustomerCost() {

        if (Objects.isNull(unitCustomerCost) || Objects.isNull(quantity)) {
            return BigDecimal.ZERO;
        } else if (Objects.isNull(totalCustomerCost)) {
            if(customerPayType == CustomerPayTypeEnum.P) {
                BigDecimal payValue = unitCustomerCost.divide(BigDecimal.valueOf(100), AppConstants.DEFAULT_SCALE, RoundingMode.UP);
                totalCustomerCost = getTotalValue().multiply(payValue);
            } else if(customerPayType == CustomerPayTypeEnum.V) {
                totalCustomerCost = unitCustomerCost.multiply(BigDecimal.valueOf(quantity));
            }
        }

        return totalCustomerCost;
    }
}