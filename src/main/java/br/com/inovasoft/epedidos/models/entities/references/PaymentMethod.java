package br.com.inovasoft.epedidos.models.entities.references;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import io.smallrye.common.constraint.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

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

    @NotNull
    @Column(name = "nome")
    private String name;
    
    @NotNull
    @Column(name = "sistema_id")
    private Long systemId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "situacao")
    private StatusEnum status;

    @NotNull
    @Column(name = "prazo")
    private Integer deadline;

    @Column(name = "baixa_automatica")
    private boolean autoPayment;

    @PrePersist
    public void prePersist() {
        if(Objects.isNull(deadline)) {
            deadline = 0;
        }
    }

}