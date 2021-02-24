package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.dtos.serializers.MoneySerializer;
import br.com.inovasoft.epedidos.models.enums.PayMethodEnum;
import br.com.inovasoft.epedidos.models.enums.PurchaseEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseDto {

    private Long id;
    private UserPortalDto buyer;
    private SupplierDto supplier;
    private String dateRef;
    private PurchaseEnum status;
    private List<PurchaseItemDto> itens;
    private String dueDate;
    private String createdOn;
    private Integer payNumber;
    private PayMethodEnum payMethod;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalValue;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal averageValue;
    private Integer totalQuantity;
}