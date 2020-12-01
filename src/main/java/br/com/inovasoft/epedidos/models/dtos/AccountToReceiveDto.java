package br.com.inovasoft.epedidos.models.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import lombok.Data;

@Data
public class AccountToReceiveDto {

    private Long id;
    private String idCustomer;
    private String nameCustomer;
    private BigDecimal originalValue;
    private BigDecimal taxValue;
    private LocalDate dueDate;
    private BigDecimal ReceiveValue;
    private LocalDate ReceiveDate;
    private StatusEnum status;
    private String note;

}