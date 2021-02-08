package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.CustomerPayTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerBillingDto {

    private Long id;
    private String name;
    private CustomerPayTypeEnum payType;
    private BigDecimal payValue;
    private BigDecimal totalValue;
    private BigDecimal customerValue;
    private BigDecimal shippingCost;
    private BigDecimal productsValue;
    private Integer quantity;
    private List<PurchaseDistributionDto> purchaseDistributions = new ArrayList<>();

    public BigDecimal getTotalValue() {
        return customerValue.add(shippingCost).add(productsValue);
    }
}