package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import br.com.inovasoft.epedidos.models.enums.converters.PayStatusEnumConverter;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "conta_pagar")
public class AccountToPay extends BaseEntity implements Billing {

    private static final long serialVersionUID = 904648166038765L;

    @Id
    @SequenceGenerator(name = "account-to-pay-sequence", sequenceName = "conta_pagar_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account-to-pay-sequence")
    private Long id;

    @NotNull
    @JoinColumn(name = "fornecedor_id")
    @ManyToOne(targetEntity = Supplier.class)
    private Supplier supplier;

    @NotNull
    @Column(name = "valor")
    private BigDecimal originalValue;

    @Column(name = "tax")
    private BigDecimal taxValue;

    @NotNull
    @Column(name = "data_vencimento")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "valor_pago")
    private BigDecimal paidOutValue;

    @Column(name = "data_pagamento")
    private LocalDate paidOutDate;

    @Column(name = "observacao")
    private String note;

    @Column(name = "sistema_id")
    private Long systemId;

    @Column(name = "situacao")
    @Getter(AccessLevel.NONE)
    @Convert(converter = PayStatusEnumConverter.class)
    private PayStatusEnum status;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if(Objects.isNull(paidOutValue)) {
            paidOutValue = BigDecimal.ZERO;
        }
        if(Objects.isNull(taxValue)) {
            taxValue = BigDecimal.ZERO;
        }
    }

}