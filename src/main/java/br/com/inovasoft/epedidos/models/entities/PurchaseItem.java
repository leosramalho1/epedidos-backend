package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "compra_item")
public class PurchaseItem extends BaseEntity {

    private static final long serialVersionUID = 1998008985093338252L;

    @Id
    @SequenceGenerator(name = "compra-item-sequence", sequenceName = "compra_item_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compra-item-sequence")
    private Long id;

    @NotNull(message = "Order is required")
    @JoinColumn(name = "compra_id")
    @ManyToOne(targetEntity = Purchase.class)
    private Purchase purchase;

    @NotNull(message = "Product is required")
    @JoinColumn(name = "produto_id")
    @OneToOne(targetEntity = Product.class)
    private Product product;

    @Column(name = "quantidade")
    private Integer quantity;

    @Column(name = "valor_unitario")
    private BigDecimal unitValue;

    @Column(name = "valor_total")
    private BigDecimal totalValue;

    @NotNull
    @Column(name = "valor_cobrado")
    private BigDecimal valueCharged;

    @PreUpdate
    @PrePersist
    public void prePersist() {
        if(Objects.isNull(totalValue)
                && Objects.nonNull(unitValue) && Objects.nonNull(quantity)) {
            totalValue = unitValue.multiply(BigDecimal.valueOf(quantity));
        }
    }

}