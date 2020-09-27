package br.com.inovasoft.epedidos.models.entities.references;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "metodo_pagamento")
public class PaymentMethod extends BaseEntity {

    private static final long serialVersionUID = -1301077848186850996L;

    @Id
    @SequenceGenerator(name = "payment-method-sequence", sequenceName = "metodo_pagamento_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment-method-sequence")
    private Long id;

    @Column(name = "nome")
    private String name;

}