package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountToPayDto implements BillingDto {

    private Long id;
    private SupplierDto supplier;
    private BigDecimal originalValue;
    private BigDecimal taxValue;
    private LocalDate dueDate;
    private BigDecimal paidOutValue;
    private LocalDate paidOutDate;
    private PayStatusEnum status;
    private String note;
    private String updatedOn;
    private List<AccountToPayDto> history = new ArrayList<>();
    private PurchaseDto purchaseDto;
    private PaymentMethodDto paymentMethod;
    private Long purchaseId;
    private BigDecimal amountPaid;

}