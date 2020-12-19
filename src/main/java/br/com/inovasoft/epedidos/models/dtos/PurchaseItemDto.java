package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseItemDto {

    private Long idProduct;
    private String nameProduct;
    private Integer quantity;
    private BigDecimal unitValue;
    private BigDecimal totalValue;
    private PackageTypeEnum packageType;

    public PurchaseItemDto() {

    }

    public PurchaseItemDto(Long idProduct, String nameProduct, Integer quantity) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
    }
}