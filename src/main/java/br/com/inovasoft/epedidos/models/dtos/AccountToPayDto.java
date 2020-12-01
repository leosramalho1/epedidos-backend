package br.com.inovasoft.epedidos.models.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import lombok.Data;

@Data
public class AccountToPayDto {

    private Long id;
    private String idSupplier;
    private String nameSupplier;
    private BigDecimal originalValue;
    private BigDecimal taxValue;
    private LocalDate dueDate;
    private BigDecimal ReceiveValue;
    private LocalDate ReceiveDate;
    private StatusEnum status;
    private String note;

}