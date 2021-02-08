package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "pedido_item", indexes = {
        @Index(name = "pedido_item_index_pedido", columnList = "pedido_id"),
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

    @Column(name = "valor_frete_unitario")
    private BigDecimal unitShippingCost;

    @Column(name = "quantidade_adiquirida")
    private Integer realizedAmount;

    @Column(name = "peso")
    private BigDecimal weidth;

    @Column(name = "quantidade_faturada")
    private Integer billedQuantity;


    @PreUpdate
    @PrePersist
    public void prePersist() {
        if(Objects.isNull(weidth)) {
            weidth = Objects.isNull(product) ? null : product.getWeidth();
        }

        if(Objects.isNull(billedQuantity)) {
            addBilledQuantity(0);
        } else if(billedQuantity > Optional.ofNullable(realizedAmount).orElse(quantity)){
            throw new ValidationException("A quantidade faturada não pode ser maior que a quantidade pedida.");
        }

    }

    public boolean hasQuantityToBilled() {
        return calculateRemainingQuantity() > 0;
    }

    public Integer calculateRemainingQuantity() {
        return Optional.ofNullable(realizedAmount).orElse(quantity) - Optional.ofNullable(billedQuantity).orElse(0);
    }

    public void addBilledQuantity(Integer billedQuantity) {
        if(Objects.isNull(this.billedQuantity)) {
            this.billedQuantity = 0;
        }
        this.billedQuantity += billedQuantity;
    }

}