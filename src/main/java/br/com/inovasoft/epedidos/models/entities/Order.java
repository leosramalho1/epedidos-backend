package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "pedido", indexes = { @Index(name = "pedido_index_cliente", columnList = "cliente_id"),
        @Index(name = "pedido_index_loja", columnList = "sistema_id") })
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
    @Column(name = "quantidade_total")
    private Integer totalProducts;

    @Column(name = "quantidade_total_adiquirida")
    private Integer totalProductsRealized;

    @NotNull
    @Column(name = "valor_bruto_total")
    private BigDecimal totalValueProducts;

    @NotNull
    @Column(name = "valor_liquido_total")
    private BigDecimal totalLiquidProducts;

    @Column(name = "valor_bruto_total_adquirido")
    private BigDecimal totalValueProductsRealized;

    @Column(name = "valor_liquido_total_adquirido")
    private BigDecimal totalLiquidProductsRealized;

    @NotNull
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private OrderEnum status;
}