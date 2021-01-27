package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.dtos.serializers.MoneySerializer;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseDto {

    private Long id;
    private UserPortalDto buyer;
    private SupplierDto supplier;
    private String dateRef;
    private OrderEnum status;
    private List<PurchaseItemDto> itens;
    private String dueDate;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal valueCharged;
    private String createdOn;
    private Integer payNumber;
    private String payMethod;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalValue;

  
    @JsonSerialize(using = MoneySerializer.class)
    public BigDecimal getAverageValue() {
        if(CollectionUtils.isNotEmpty(itens)) {
            Integer quantityTemp = getTotalQuantity();
            return getTotalValue()
                    .divide(BigDecimal.valueOf(quantityTemp>0?quantityTemp:1), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    public Integer getTotalQuantity() {
        if(CollectionUtils.isNotEmpty(itens)) {
            return itens.stream()
                    .map(PurchaseItemDto::getQuantity)
                    .filter(Objects::nonNull)
                    .reduce(0, Integer::sum);
        }

        return 0;
    }
}