package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "conta_receber", indexes = {
        @Index(name = "conta_receber_index_sistema", columnList = "sistema_id"),
        @Index(name = "conta_receber_index_cliente", columnList = "cliente_id") })
@Audited
public class AccountToReceive extends BaseEntity implements Billing {

    private static final long serialVersionUID = 904648166038765L;

    @Id
    @SequenceGenerator(name = "AccountToReceive-sequence", sequenceName = "AccountToReceive_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountToReceive-sequence")
    private Long id;

    @NotNull
    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "cliente_id")
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;

    @Column(name = "valor", scale = 2)
    private BigDecimal originalValue;

    @Column(name = "tax", scale = 2)
    private BigDecimal taxValue;

    @Column(name = "data_vencimento")
    private LocalDate dueDate;

    @Column(name = "valor_pago", scale = 2)
    private BigDecimal paidOutValue;

    @Column(name = "data_recebimento")
    private LocalDate paidOutDate;

    @Column(name = "observacao")
    private String note;

    @Column(name = "sistema_id")
    private Long systemId;

    @NotNull
    @Column(name = "situacao")
    @Getter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
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

        status = getStatus();

    }


}