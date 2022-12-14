package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.configuration.AppConstants;
import br.com.inovasoft.epedidos.exceptions.IllegalCustomerPayTypeException;
import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.CustomerPayTypeEnum;
import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
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
@Table(name = "compra_distribuicao", indexes = {
        @Index(name = "compra_distribuicao_index_pedido_item", columnList = "pedido_item_id"),
        @Index(name = "compra_distribuicao_index_produto", columnList = "produto_id"),
        @Index(name = "compra_item_distribuicao_index_compra_item", columnList = "compra_item_id"),
        @Index(name = "compra_distribuicao_index_cliente", columnList = "cliente_id") })
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
    @Column(name = "quantidade_distribuida")
    private Integer quantity;

    @NotNull
    @Column(name = "valor_unitario", scale = AppConstants.MONEY_SCALE)
    private BigDecimal valueCharged;

    @NotNull
    @Column(name = "valor_cliente_unitario")
    private BigDecimal unitCustomerCost;

    @NotNull
    @Column(name = "valor_frete")
    private BigDecimal unitShippingCost;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cobranca_cliente")
    private CustomerPayTypeEnum customerPayType;

    @Column(name = "tipo_embalagem")
    @Enumerated(EnumType.STRING)
    private PackageTypeEnum packageType;

    @NotNull
    @JoinColumn(name = "produto_id")
    @ManyToOne(targetEntity = Product.class)
    private Product product;

    @PreUpdate
    @PrePersist
    public void prePersist() {
        if(Objects.isNull(quantity)) {
            quantity = 0;
        }
    }

    public BigDecimal calculateTotalValue() {

        if(quantity > 0) {
            return valueCharged.multiply(BigDecimal.valueOf(quantity)).setScale(AppConstants.MONEY_SCALE, RoundingMode.UP);
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal calculateCustomerTotalValue() {
        BigDecimal customerValue;

        if (getCustomerPayType() == CustomerPayTypeEnum.V) {
            customerValue = getValueCharged()
                    .add(getUnitCustomerCost())
                    .multiply(BigDecimal.valueOf(getQuantity()));
        } else if (getCustomerPayType() == CustomerPayTypeEnum.P) {
            BigDecimal payValue = getUnitCustomerCost()
                    .divide(BigDecimal.valueOf(100), AppConstants.MONEY_SCALE, RoundingMode.UP)
                    .add(BigDecimal.ONE);

            customerValue = getValueCharged()
                    .multiply(BigDecimal.valueOf(getQuantity()))
                    .multiply(payValue);
        } else {
            throw new IllegalCustomerPayTypeException();
        }

        return customerValue.setScale(AppConstants.MONEY_SCALE, RoundingMode.UP);
    }

    public BigDecimal calculateCustomerValue() {
        return calculateCustomerTotalValue().subtract(calculateTotalValue());
    }

}