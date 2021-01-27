package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.dtos.serializers.MoneySerializer;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountToReceiveDto implements BillingDto {

    private Long id;
    private CustomerDto customer;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal originalValue;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal taxValue;
    private LocalDate dueDate;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal paidOutValue;
    private LocalDate paidOutDate;
    private PayStatusEnum status;
    private String note;

}