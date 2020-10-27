package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDto {

    private Long id;
    private Long idProduct;
    private Long nameProduct;
    private Integer quantity;

}