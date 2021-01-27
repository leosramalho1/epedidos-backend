package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import br.com.inovasoft.epedidos.models.dtos.serializers.MoneySerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseItemDto {

    private Long idProduct;
    private String nameProduct;
    private Integer quantity;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal unitValue;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalValue;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal valueCharged;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal averageValue;

    public PurchaseItemDto(Long idProduct, String nameProduct, Integer quantity) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
    }

}