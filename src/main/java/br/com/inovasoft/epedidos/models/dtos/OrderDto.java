package br.com.inovasoft.epedidos.models.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDto {

    private Long id;
    private List<Long> idCustomers;
    private Long idCustomer;
    private String nameCustomer;
    private String createdOn;
    private OrderEnum status;
    private List<OrderItemDto> itens;
}