package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@Table(name = "compra_distribuicao")
public class PurchaseDistribution extends BaseEntity {

    private static final long serialVersionUID = 7699908322410433370L;

    public PurchaseDistribution(Long id) {
        this.id = id;
    }

    @Id
    @SequenceGenerator(name = "compra-distribuicao-sequence", sequenceName = "compra_distribuicao_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compra-distribuicao-sequence")
    private Long id;

    @NotNull(message = "Sistema deve ser informado")
    @Column(name = "sistema_id")
    private Long systemId;

    @NotNull
    @JoinColumn(name = "compra_item_id")
    @ManyToOne(targetEntity = PurchaseItem.class)
    private PurchaseItem purchaseItem;

    @NotNull
    @JoinColumn(name = "pedido_item_id")
    @ManyToOne(targetEntity = OrderItem.class)
    private OrderItem orderItem;

    @NotNull
    @JoinColumn(name = "cliente_id")
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;

    @JoinColumn(name = "conta_receber_id")
    @ManyToOne(targetEntity = AccountToReceive.class)
    private AccountToReceive accountToReceive;

    @NotNull
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private OrderEnum status;

    @NotNull
    @Column(name = "quantidade_distribuida")
    private Integer quantity;

    @NotNull
    @Column(name = "valor_unitario", scale = 4)
    private BigDecimal valueCharged;

//    @NotNull
//    @Column(name = "valor_frete_unitario")
//    private BigDecimal unitShippingCost;


    @PreUpdate
    @PrePersist
    public void prePersist() {
        if(Objects.isNull(quantity)) {
            quantity = 0;
        }
    }

    public BigDecimal calculateTotalValue() {

        if(quantity > 0) {
            return valueCharged.multiply(BigDecimal.valueOf(quantity)).setScale(4, RoundingMode.UP);
        }

        return BigDecimal.ZERO;
    }

}