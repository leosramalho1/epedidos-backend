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

    private Integer quantity;

    @Column(name = "quantidade_adiquirida")
    private Integer realizedAmount;

    private BigDecimal unitValue;

    private BigDecimal totalValue;

    @Column(name = "peso")
    private BigDecimal weidth;

    public BigDecimal getWeidth() {
        if(!Objects.isNull(weidth)) {
            return weidth;
        } else {
            return Objects.isNull(product) ? null : product.getWeidth();
        }
    }

}