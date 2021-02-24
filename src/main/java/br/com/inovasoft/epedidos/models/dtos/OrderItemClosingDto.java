package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemClosingDto {

    private Long id;
    private Long idProduct;
    private String nameProduct;
    private Integer realizedAmount;
    private BigDecimal unitValue;
    private BigDecimal valueCharged;
    private BigDecimal totalValue;
    private BigDecimal unitShippingCost;


}