package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "pedido", indexes = {
        @Index(name = "pedido_index_sistema", columnList = "sistema_id"),
        @Index(name = "pedido_index_cliente", columnList = "cliente_id"),
        @Index(name = "pedido_index_situacao", columnList = "situacao") })
public class Order extends BaseEntity {

    private static final long serialVersionUID = 7699908322410433370L;

    @Id
    @SequenceGenerator(name = "order-sequence", sequenceName = "pedido_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order-sequence")
    private Long id;

    @Column(name = "sistema_id")
    private Long systemId;

    @NotNull
    @JoinColumn(name = "cliente_id")
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;

    @NotNull
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private OrderEnum status;

    @ToString.Exclude
    @OneToMany(targetEntity = OrderItem.class, mappedBy = "order", orphanRemoval = true)
    private List<OrderItem> orderItems;

    @PreUpdate
    @PrePersist
    public void prePersist() {
        if(CollectionUtils.isNotEmpty(orderItems) && !hasQuantityToBilled()) {
            status = OrderEnum.FINISHED;
        }
    }

    public boolean hasQuantityToBilled() {
        if(CollectionUtils.isNotEmpty(orderItems)) {
            return orderItems.stream().anyMatch(OrderItem::hasQuantityToBilled);
        }

        return false;
    }
}