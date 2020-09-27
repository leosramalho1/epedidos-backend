package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "pagamento_prazo")
public class PaymentTime extends BaseEntity {

    private static final long serialVersionUID = -8368683319896901309L;

    @Id
    @SequenceGenerator(name = "payment-time-sequence", sequenceName = "pagamento_prazo_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment-time-sequence")
    private Long id;

    @NotNull
    @JoinColumn(name = "sistema_id")
    @ManyToOne(targetEntity = CompanySystem.class)
    private CompanySystem system;

    @Column(name = "descricao")
    private String description;

    @Column(name = "quantidade_parcelas")
    private Integer payNumbers;

}