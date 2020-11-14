package br.com.inovasoft.epedidos.models.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    @NotNull
    @Column(name = "valor_bruto_total")
    private BigDecimal totalValueProducts;

    @NotNull
    @Column(name = "valor_liquido_total")
    private BigDecimal totalLiquidProducts;

    @NotNull
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private OrderEnum status;
}