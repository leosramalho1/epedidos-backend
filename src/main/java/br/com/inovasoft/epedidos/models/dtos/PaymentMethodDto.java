package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import lombok.Data;

@Data
public class PaymentMethodDto {

    private Long id;
    private String name;
    private StatusEnum status;
    private Integer deadline;
    
}