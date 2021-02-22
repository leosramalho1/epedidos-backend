package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import javax.persistence.*;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "compra_item", indexes = {
        @Index(name = "compra_item_index_compra", columnList = "compra_id"),
        @Index(name = "compra_index_produto", columnList = "produto_id") })
public class PurchaseItem extends BaseEntity {

    private static final long serialVersionUID = 1998008985093338252L;

    public PurchaseItem(Long product, Long quantity, BigDecimal totalValue, BigDecimal unitValue, BigDecimal sumValueCharged) {
        this.product = Product.findById(product);
        this.quantity = Optional.ofNullable(quantity).orElse(0L).intValue();
        this.unitValue = Objects.nonNull(unitValue) ? unitValue.setScale(4, RoundingMode.UP) : null;
        this.averageValue = totalValue.divide(BigDecimal.valueOf(NumberUtils.max(1, quantity)), 4, RoundingMode.UP);
        this.valueCharged = sumValueCharged.divide(BigDecimal.valueOf(NumberUtils.max(1, quantity)), 4, RoundingMode.UP);
        this.totalValue = valueCharged.multiply(BigDecimal.valueOf(this.quantity)).setScale(4, RoundingMode.UP);
    }

    @Id
    @SequenceGenerator(name = "compra-item-sequence", sequenceName = "compra_item_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compra-item-sequence")
    private Long id;

    @NotNull(message = "Purchase is required")
    @JoinColumn(name = "compra_id")
    @ManyToOne(targetEntity = Purchase.class)
    private Purchase purchase;

    @NotNull(message = "Product is required")
    @JoinColumn(name = "produto_id")
    @OneToOne(targetEntity = Product.class)
    private Product product;

    @NotNull(message = "Quantidade do item não pode ser nulo")
    @Column(name = "quantidade")
    private Integer quantity;

    @NotNull(message = "Valor unitario do item não pode ser nulo")
    @Column(name = "valor_unitario", scale = 4)
    private BigDecimal unitValue;

    @NotNull(message = "Valor unitario do item não pode ser nulo")
    @Column(name = "valor_cobrado", scale = 4)
    private BigDecimal valueCharged;

    @NotNull
    @Column(name = "quantidade_distribuida")
    private Integer distributedQuantity;

    @Transient
    private BigDecimal averageValue;

    @Transient
    private BigDecimal totalValue;

    @PreUpdate
    @PrePersist
    public void prePersist() {
        if(Objects.isNull(distributedQuantity)) {
            addDistributedQuantity(0);
        } else if(distributedQuantity > quantity){
            throw new ValidationException("A quantidade distribuída não pode ser maior que a quantidade adquirida.");
        }

        totalValue = calculateTotalValue();
        if(Objects.isNull(valueCharged)) {
            this.valueCharged = this.unitValue;
        }
    }

    @PostLoad
    public void postLoad() {
        totalValue = calculateTotalValue();
    }

    public BigDecimal calculateTotalValue() {
        return Optional.ofNullable(valueCharged).orElse(unitValue).multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.UP);
    }

    public BigDecimal calculateAverageValue() {
        BigDecimal totalValue = calculateTotalValue();
        if(totalValue.intValue() > 0){
            Integer quantity = Optional.ofNullable(this.quantity).orElse(1);
            return totalValue
                    .divide(BigDecimal.valueOf(quantity > 0 ? quantity : 1), 2, RoundingMode.UP);
        }

        return BigDecimal.ZERO;

    }

    public boolean hasQuantityToDistributed() {
        return calculateAvaliableQuantity() > 0;
    }

    public Integer calculateAvaliableQuantity() {
        return quantity - distributedQuantity;
    }

    public void addDistributedQuantity(Integer distributedQuantity) {
        if(Objects.isNull(this.distributedQuantity)) {
            this.distributedQuantity = 0;
        }
        this.distributedQuantity += distributedQuantity;
    }

}