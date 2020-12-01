package br.com.inovasoft.epedidos.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "conta_receber")
public class AccountToReceive extends BaseEntity {

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
    private BigDecimal ReceiveValue;

    @Column(name = "data_vencimento")
    private LocalDate ReceiveDate;

    @NotNull(message = "Status is required")
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "observacao")
    private String note;

    @Column(name = "sistema_id")
    private Long systemId;
}