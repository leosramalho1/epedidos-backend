package br.com.inovasoft.epedidos.models.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BillingDto {

    BigDecimal getOriginalValue();

    BigDecimal getTaxValue();

    LocalDate getDueDate();

    BigDecimal getPaidOutValue();

    LocalDate getPaidOutDate();
}