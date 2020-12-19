package br.com.inovasoft.epedidos.models.dtos;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class PackageLoanDto {

    private Long id;
    private Long borrowedAmount;
    private Long returnedAmount;
    private Long remainingAmount;
    private SupplierDto supplier;
    private CustomerDto customer;
    private OrderItemDto orderItem;
    private String updatedOn;

    private String responsibleName;
    private String responsibleType;
    private List<PackageLoanDto> history;

    public Long getRemainingAmount() {
        return borrowedAmount-returnedAmount;
    }

    public String getResponsibleName() {

        String supplierName = null;
        String customerName = null;

        if(!Objects.isNull(supplier)) {
            supplierName = supplier.getName();
        }

        if(!Objects.isNull(customer)) {
            customerName = customer.getName();
        }

        return Objects.toString(supplierName, customerName);

    }
}