package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountToPayDto implements BillingDto {

    private Long id;
    private SupplierDto supplier;
    private BigDecimal originalValue;
    private BigDecimal taxValue;
    private LocalDate dueDate;
    private BigDecimal payValue;
    private LocalDate payDate;
    private PayStatusEnum status;
    private String note;


    @Override
    public BigDecimal getPaidOut() {
        return payValue;
    }

    @Override
    public LocalDate getPaidOutDate() {
        return payDate;
    }
}