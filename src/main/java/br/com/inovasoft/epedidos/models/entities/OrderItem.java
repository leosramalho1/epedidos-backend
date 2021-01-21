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
@Table(name = "pedido_item", indexes = { @Index(name = "pedido_item_index_pedido", columnList = "pedido_id"),
        @Index(name = "pedido_item_index_produto", columnList = "produto_id") })
public class OrderItem extends BaseEntity {

    private static final long serialVersionUID = 1998008985093338252L;

    @Id
    @SequenceGenerator(name = "order-item-sequence", sequenceName = "pedido_item_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order-item-sequence")
    private Long id;

    @NotNull(message = "Order is required")
    @JoinColumn(name = "pedido_id")
    @ManyToOne(targetEntity = Order.class)
    private Order order;

    @NotNull(message = "Product is required")
    @JoinColumn(name = "produto_id")
    @OneToOne(targetEntity = Product.class)
    private Product product;

    @NotNull
    @Column(name = "quantidade")
    private Integer quantity;

    @Column(name = "quantidade_adiquirida")
    private Integer realizedAmount;

    @NotNull
    @Column(name = "valor_unitario")
    private BigDecimal unitValue;

    @NotNull
    @Column(name = "valor_total")
    private BigDecimal totalValue;

    @Column(name = "peso")
    private BigDecimal weidth;

    @NotNull
    @Column(name = "valor_unitario_frete")
    private BigDecimal unitShippingCost;

    @PreUpdate
    @PrePersist
    public void prePersist() {
        if(Objects.isNull(weidth)) {
            weidth = Objects.isNull(product) ? null : product.getWeidth();
        }

        if(Objects.isNull(unitShippingCost)) {
            unitShippingCost = Objects.isNull(product) ? BigDecimal.ZERO : product.getShippingCost();
        }

        if(Objects.isNull(unitValue)) {
            unitValue = BigDecimal.ZERO;
        }

        if(Objects.isNull(unitShippingCost)) {
            unitShippingCost = BigDecimal.ZERO;
        }

        if(Objects.isNull(totalValue)) {
            totalValue = unitValue.add(unitShippingCost).multiply(BigDecimal.valueOf(quantity));
        }

       

    }

}