package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
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
        @Index(name = "pedido_index_loja", columnList = "sistema_id"),
        @Index(name = "pedido_index_endereco_cliente", columnList = "endereco_cliente_id") })
public class Order extends BaseEntity {

    private static final long serialVersionUID = 7699908322410433370L;

    @Id
    @SequenceGenerator(name = "order-sequence", sequenceName = "pedido_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order-sequence")
    private Long id;

    @NotNull
    @JoinColumn(name = "sistema_id")
    @ManyToOne(targetEntity = CompanySystem.class)
    private CompanySystem system;

    @NotNull
    @JoinColumn(name = "cliente_id")
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;

    @NotNull(message = "Payment is required")
    @JoinColumn(name = "forma_pagamento_id")
    @OneToOne(targetEntity = Payment.class)
    private Payment payment;

    @NotNull(message = "PaymentTime is required")
    @JoinColumn(name = "prazo_pagamento_id")
    @OneToOne(targetEntity = PaymentTime.class)
    private PaymentTime prazoPagamento;

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
    @JoinColumn(name = "endereco_cliente_id")
    @ManyToOne(targetEntity = CustomerAddress.class)
    private CustomerAddress customerAddress;

}