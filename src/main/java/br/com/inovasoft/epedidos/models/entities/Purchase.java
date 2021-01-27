package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "compra")
public class Purchase extends BaseEntity {

    private static final long serialVersionUID = 7699908322410433370L;

    public Purchase(Long id, Long idSupplier, String nameSupplier, LocalDateTime createdOn, BigDecimal totalValue ){
        this.id = id;
        this.supplier = new Supplier();
        this.supplier.setId(idSupplier);
        this.supplier.setName(nameSupplier);
        super.setCreatedOn(createdOn);
        this.totalValue = totalValue;
    }

    @Id
    @SequenceGenerator(name = "compra-sequence", sequenceName = "compra_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compra-sequence")
    private Long id;

    @Column(name = "sistema_id")
    private Long systemId;

    @NotNull
    @JoinColumn(name = "comprador_id")
    @ManyToOne(targetEntity = UserPortal.class)
    private UserPortal buyer;

    @NotNull
    @JoinColumn(name = "fornecedor_id")
    @ManyToOne(targetEntity = Supplier.class)
    private Supplier supplier;

    @NotNull
    @Column(name = "valor_total")
    private BigDecimal totalValue;

    @NotNull
    @Column(name = "valor_cobrado")
    private BigDecimal valueCharged;

    @NotNull
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private OrderEnum status;

    @NotNull
    @Column(name = "data_vencimento")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "quantidade_parcela")
    private Integer payNumber;

    @NotNull
    @Column(name = "forma_pagamento")
    private String payMethod;

    @OneToMany(targetEntity = PurchaseItem.class, mappedBy = "purchase")
    private List<PurchaseItem> itens;
    
    @Transient
    String supplierName;

    @PreUpdate
    @PrePersist
    public void prePersist(){
        if(Objects.isNull(totalValue)) {
            totalValue = calculateTotalValue();
        }
        if(Objects.isNull(valueCharged)) {
            valueCharged = calculateValueCharged();
        }
    }

    public BigDecimal calculateTotalValue() {
        if(CollectionUtils.isNotEmpty(itens)) {
            return itens.stream()
                    .map(PurchaseItem::getTotalValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal calculateValueCharged() {
        if(CollectionUtils.isNotEmpty(itens)) {
            return itens.stream()
                    .map(PurchaseItem::getValueCharged)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return BigDecimal.ZERO;
    }

}