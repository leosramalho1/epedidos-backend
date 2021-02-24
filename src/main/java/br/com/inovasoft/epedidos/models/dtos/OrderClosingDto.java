package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderClosingDto {

    private Long id;
    private String nameCustomer;
    private String createdOn;
    private OrderEnum status;
    private List<OrderItemDto> orderItems;
    private Integer updates;
    private boolean editable = true;

    public boolean isEditable() {
        return editable && status == OrderEnum.OPEN;
    }
}