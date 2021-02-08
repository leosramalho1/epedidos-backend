package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDto {

    private Long id;
    private Long idProduct;
    private String nameProduct;
    private Integer quantity;

    public OrderItemDto() {
        super();
    }

    public OrderItemDto(Long idProduct, String nameProduct) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = 0;
    }

}