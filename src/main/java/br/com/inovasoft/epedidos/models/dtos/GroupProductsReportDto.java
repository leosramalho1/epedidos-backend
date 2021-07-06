package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupProductsReportDto {

    private ProductReportDto first;
    private ProductReportDto second;

    public void init() {
        first = Optional.ofNullable(first).orElse(ProductReportDto.builder().build());
        second = Optional.ofNullable(second).orElse(ProductReportDto.builder().build());
    }

    public BigDecimal totalValue() {
        init();
        return Optional.ofNullable(first.getTotalValueNumeric()).orElse(BigDecimal.ZERO)
                .add(Optional.ofNullable(second.getTotalValueNumeric()).orElse(BigDecimal.ZERO));
    }

    public Long totalPackageLoan() {
        init();
        return Optional.ofNullable(first.getPackageLoan()).orElse(0L) +
                Optional.ofNullable(second.getPackageLoan()).orElse(0L);
    }

}