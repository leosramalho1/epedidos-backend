package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.entities.references.PaymentMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "pagamento", indexes = { @Index(name = "pagamento_index_pedido", columnList = "pedido_id"),
        @Index(name = "pagamento_index_metodo_pagamento", columnList = "metodo_pagamento_id") })
public class Payment extends BaseEntity {

    private static final long serialVersionUID = -3602025032372070888L;

    @Id
    @SequenceGenerator(name = "payment-sequence", sequenceName = "pagamento_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment-sequence")
    private Long id;

	@NotNull(message = "Order is required")
    @JoinColumn(name = "pedido_id")
    @ManyToOne(targetEntity = Order.class)
    private Order order;

    @NotNull(message = "Method is required")
    @JoinColumn(name = "metodo_pagamento_id")
    @OneToOne(targetEntity = PaymentMethod.class)
    private PaymentMethod paymentMethod;

}