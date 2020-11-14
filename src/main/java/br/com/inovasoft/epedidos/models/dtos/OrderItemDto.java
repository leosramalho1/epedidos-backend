package br.com.inovasoft.epedidos.models.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDto {

    public OrderItemDto() {
        super();
    }

    public OrderItemDto(Long idProduct, String nameProduct, BigDecimal unitValue) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = 1;
        this.unitValue = unitValue;
        this.totalValue = this.unitValue.multiply(BigDecimal.ONE);
    }

    private Long id;
    private Long idProduct;
    private String nameProduct;
    private Integer quantity;
    private BigDecimal unitValue;
    private BigDecimal totalValue;

}