package br.com.inovasoft.epedidos.models.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {

    private Long id;
    private Long idCustomer;
    private Long nameCustomer;
    private String createdDate;
    private OrderEnum status;
    private List<OrderItemDto> itens;
}