package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.configuration.AppConstants;
import br.com.inovasoft.epedidos.util.FormatUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductReportDto {

    private Long id;
    private String name;
    private String weight;
    private Long quantity;
    private String unitValue;
    private String totalValue;
    private BigDecimal totalValueNumeric;
    private String profitMargin;
    private String cost;
    private String priceSale;
    private Long packageLoan;

    public ProductReportDto(Long id, String name, BigDecimal weight, Long quantity, BigDecimal unitValue, BigDecimal profitMargin, Long packageLoan) {
        profitMargin = Optional.ofNullable(profitMargin).orElse(BigDecimal.ONE);

        BigDecimal weightNumeric = weight.setScale(0, RoundingMode.HALF_UP);
        BigDecimal unitValueNumeric = Optional.ofNullable(unitValue).orElse(BigDecimal.ZERO).setScale(AppConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);
        this.totalValueNumeric = BigDecimal.valueOf(quantity).multiply(unitValueNumeric);
        BigDecimal costNumeric = unitValueNumeric.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : unitValueNumeric
                .divide(weightNumeric, AppConstants.MONEY_SCALE, RoundingMode.HALF_UP);
        BigDecimal priceSaleNumeric = costNumeric.multiply(profitMargin)
                .setScale(AppConstants.DEFAULT_SCALE, RoundingMode.HALF_UP);

        this.id = id;
        this.name = name;
        this.weight = FormatUtil.formataNumero(weightNumeric);
        this.quantity = quantity;
        this.unitValue = FormatUtil.formataValor(unitValueNumeric);
        this.totalValue = FormatUtil.formataValor(totalValueNumeric);
        this.cost = FormatUtil.formataValor(costNumeric);
        this.priceSale = FormatUtil.formataValor(priceSaleNumeric);
        this.packageLoan = packageLoan;
    }
}