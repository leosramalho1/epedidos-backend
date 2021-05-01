package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.configuration.AppConstants;
import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.entities.references.PaymentMethod;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "conta_pagar", indexes = {
        @Index(name = "conta_pagar_index_sistema", columnList = "sistema_id"),
        @Index(name = "conta_pagar_index_fornecedor", columnList = "fornecedor_id"),
        @Index(name = "conta_pagar_index_fornecedor_sistema", columnList = "fornecedor_id, sistema_id") })
public class AccountToPay extends BaseEntity implements Billing {

    private static final long serialVersionUID = 904648166038765L;

    @Id
    @SequenceGenerator(name = "account-to-pay-sequence", sequenceName = "conta_pagar_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account-to-pay-sequence")
    private Long id;

    @NotNull
    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "fornecedor_id")
    @ManyToOne(targetEntity = Supplier.class)
    private Supplier supplier;

    @NotNull
    @Column(name = "valor", scale = AppConstants.DEFAULT_SCALE)
    private BigDecimal originalValue;

    @Column(name = "tax", scale = AppConstants.DEFAULT_SCALE)
    private BigDecimal taxValue;

    @NotNull
    @Column(name = "data_vencimento")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "valor_pago", scale = AppConstants.DEFAULT_SCALE)
    private BigDecimal paidOutValue;

    @Column(name = "data_pagamento")
    private LocalDate paidOutDate;

    @Column(name = "observacao")
    private String note;

    @NotNull
    @Column(name = "sistema_id")
    private Long systemId;

    @NotNull
    @Column(name = "situacao")
    @Getter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    private PayStatusEnum status;

    @NotNull(message = "Purchase is required")
    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "compra_id")
    @ManyToOne(targetEntity = Purchase.class)
    private Purchase purchase;

    @NotNull(message = "Payment method is required")
    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "metodo_pagamento_id")
    @ManyToOne(targetEntity = PaymentMethod.class)
    private PaymentMethod paymentMethod;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if(Objects.isNull(paidOutValue)) {
            paidOutValue = BigDecimal.ZERO;
        }
        if(Objects.isNull(taxValue)) {
            taxValue = BigDecimal.ZERO;
        }

        status = getStatus();

        if(status == PayStatusEnum.PARTIALLY_PAID
                || status == PayStatusEnum.PAID
                || status == PayStatusEnum.PAID_OVERDUE) {
            Objects.requireNonNull(paidOutDate, "Data de pagamento é obrigatória");
        }
    }

}