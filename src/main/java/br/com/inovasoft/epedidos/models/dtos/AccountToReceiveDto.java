package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountToReceiveDto implements BillingDto {

    private Long id;
    private CustomerDto customer;
    private BigDecimal originalValue;
    private BigDecimal taxValue;
    private LocalDate dueDate;
    private BigDecimal receiveValue;
    private LocalDate receiveDate;
    private PayStatusEnum status;
    private String note;

    @Override
    public BigDecimal getPaidOut() {
        return receiveValue;
    }

    @Override
    public LocalDate getPaidOutDate() {
        return receiveDate;
    }

}