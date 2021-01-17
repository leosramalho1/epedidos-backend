package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "emprestimo_embalagem")
@Audited
public class PackageLoan extends BaseEntity {

    private static final long serialVersionUID = 4219610476537959071L;

    @Id
    @SequenceGenerator(name = "package-loan-sequence", sequenceName = "emprestimo_embalagem_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "package-loan-sequence")
    private Long id;

    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "order_item_id")
    @ManyToOne(targetEntity = OrderItem.class)
    private OrderItem orderItem;

    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "compra_item_id")
    @ManyToOne(targetEntity = PurchaseItem.class)
    private PurchaseItem purchaseItem;

    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "fornecedor_id")
    @ManyToOne(targetEntity = Supplier.class)
    private Supplier supplier;

    @Audited(targetAuditMode = NOT_AUDITED)
    @JoinColumn(name = "cliente_id")
    @ManyToOne(targetEntity = Customer.class)
    private Customer customer;

    @Column(name = "quantidade_emprestada")
    private Long borrowedAmount;

    @Column(name = "quantidade_devolvida")
    private Long returnedAmount;

    @Column(name = "sistema_id")
    private Long systemId;

    @NotAudited
    @Formula("quantidade_emprestada - quantidade_devolvida")
    private Long remainingAmount;

    @NotNull
    @Column(name = "usuario_alteracao")
    private String userChange;

    @PreUpdate
    @PrePersist
    public void prePersist() {
        if(returnedAmount == null) {
            returnedAmount = 0L;
        }
    }
}