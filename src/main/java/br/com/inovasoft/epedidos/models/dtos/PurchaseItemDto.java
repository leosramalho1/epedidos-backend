package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.dtos.serializers.MoneySerializer;
import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseItemDto {

    private Long id;
    private Long idProduct;
    private String nameProduct;
    private Integer quantity;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal unitValue;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal totalValue;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal averageValue;
    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal valueCharged;
    private PackageTypeEnum packageType;

    public PurchaseItemDto(Long idProduct, String nameProduct, Integer quantity, PackageTypeEnum packageType) {
        this.idProduct = idProduct;
        this.nameProduct = nameProduct;
        this.quantity = quantity;
        this.totalValue = BigDecimal.ZERO;
        this.packageType = packageType;
    }


}