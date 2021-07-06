package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderClosedReportDto {

    private CustomerDto customer;
    private List<GroupProductsReportDto> products;
    private String dateReport;
    private String totalValue;
    private String totalPackageLoan;

    public static BigDecimal calculateTotalValue(List<GroupProductsReportDto> products) {

        if(CollectionUtils.isNotEmpty(products)) {
            return products.stream()
                    .map(GroupProductsReportDto::totalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        return BigDecimal.ZERO;
    }

    public static Long calculateTotalPackageLoan(List<GroupProductsReportDto> products) {

        if(CollectionUtils.isNotEmpty(products)) {
            return products.stream()
                    .map(GroupProductsReportDto::totalPackageLoan)
                    .reduce(0L, Long::sum);
        }

        return 0L;
    }

    public BigDecimal calculateTotalValue() {
        return calculateTotalValue(this.products);
    }

    @Override
    public String toString() {
        return "Relatório de pedidos fechados - " + customer.getName() + " - " + dateReport;
    }
}