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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "conta_receber")
public class AccountToReceive extends BaseEntity implements Billing {

    private static final long serialVersionUID = 904648166038765L;

    @Id
    @SequenceGenerator(name = "AccountToReceive-sequence", sequenceName = "AccountToReceive_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountToReceive-sequence")
    private Long id;

    @NotNull
    @JoinColumn(name = "cliente_id")
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;

    @Column(name = "valor")
    private BigDecimal originalValue;

    @Column(name = "tax")
    private BigDecimal taxValue;

    @Column(name = "data_vencimento")
    private LocalDate dueDate;

    @Column(name = "valor_pago")
    private BigDecimal receiveValue;

    @Column(name = "data_recebimento")
    private LocalDate receiveDate;

    @Column(name = "observacao")
    private String note;

    @Column(name = "sistema_id")
    private Long systemId;

    @OneToOne(mappedBy = "accountToReceive")
    private Order order;

    @Column(name = "situacao")
    @Getter(AccessLevel.NONE)
    @Convert(converter = PayStatusEnumConverter.class)
    private PayStatusEnum status;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if(Objects.isNull(receiveValue)) {
            receiveValue = BigDecimal.ZERO;
        }
        if(Objects.isNull(taxValue)) {
            taxValue = BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getPaidOut() {
        return receiveValue;
    }

    @Override
    public LocalDate getPaidOutDate() {
        return receiveDate;
    }
}