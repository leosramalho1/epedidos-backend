package br.com.inovasoft.epedidos.models.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.inovasoft.epedidos.models.dtos.serializers.MoneySerializer;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;

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
    private BigDecimal averageValue;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalQuantity;
}