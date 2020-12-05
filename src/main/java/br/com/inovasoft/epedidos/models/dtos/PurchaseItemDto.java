package br.com.inovasoft.epedidos.models.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseItemDto {

    private Long idProduct;
    private String nameProduct;
    private Integer quantity;
    private BigDecimal unitValue;
    private BigDecimal totalValue;
    private String packageType;

    public PurchaseItemDto() {

    }

    public PurchaseItemDto(Long idProduct, String nameProduct, Integer quantity) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
    }
}