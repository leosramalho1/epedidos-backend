package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    private BigDecimal valueCharged;
    private String createdOn;
    private Long idSupplier;
    private Long idBuyer;
    private Long payNumber;
    private String payMethod;

    public BigDecimal getTotalValue() {
        if(CollectionUtils.isNotEmpty(itens)) {
            return itens.stream()
                    .map(PurchaseItemDto::getTotalValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

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

    public BigDecimal getValueCharged() {
        if(CollectionUtils.isNotEmpty(itens)) {
            return itens.stream()
                    .map(PurchaseItemDto::getValueCharged)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return BigDecimal.ZERO;
    }
}