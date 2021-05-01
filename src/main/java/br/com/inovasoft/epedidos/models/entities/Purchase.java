package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.configuration.AppConstants;
import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.entities.references.PaymentMethod;
import br.com.inovasoft.epedidos.models.enums.PurchaseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "compra", indexes = {
        @Index(name = "compra_index_sistema", columnList = "sistema_id"),
        @Index(name = "compra_index_comprador", columnList = "comprador_id"),
        @Index(name = "compra_index_fornecedor", columnList = "fornecedor_id"),
        @Index(name = "compra_index_situacao", columnList = "situacao") })
public class Purchase extends BaseEntity {

    private static final long serialVersionUID = 7699908322410433370L;

    public Purchase(Long id, Long idSupplier, String nameSupplier, LocalDateTime createdOn, BigDecimal totalValue){
        this.id = id;
        this.supplier = Supplier.builder().id(idSupplier).name(nameSupplier).build();
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

    @Min(value = 1, message = "O valor total deve ser maior que 0")
    @NotNull
    @Column(name = "valor_total", scale = AppConstants.MONEY_SCALE)
    private BigDecimal totalValue;

    @Min(value = 1, message = "A quantidade total deve ser maior que 0")
    @NotNull
    @Column(name = "quantidade_total")
    private Integer totalQuantity;

    @NotNull
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private PurchaseEnum status;

    @NotNull
    @Column(name = "data_vencimento")
    private LocalDate dueDate;

    @Column(name = "quantidade_parcela")
    private Integer payNumber;

    @NotNull
    @JoinColumn(name = "metodo_pagamento_id")
    @ManyToOne(targetEntity = PaymentMethod.class)
    private PaymentMethod paymentMethod;

    @ToString.Exclude
    @OneToMany(targetEntity = PurchaseItem.class, mappedBy = "purchase")
    private List<PurchaseItem> itens;

    @Transient
    private BigDecimal averageValue;

    @PreUpdate
    @PrePersist
    public void prePersist(){
        totalValue = calculateTotalValue(itens, totalValue);
        totalQuantity = calculateQuantity(itens, totalQuantity);

    }

    public static BigDecimal calculateTotalValue(List<PurchaseItem> purchaseItems, BigDecimal nullDefault) {
        if(CollectionUtils.isNotEmpty(purchaseItems)) {
            return purchaseItems.stream()
                    .map(PurchaseItem::calculateTotalValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return nullDefault;
    }

    public static Integer calculateQuantity(List<PurchaseItem> purchaseItems, Integer nullDefault) {
        if(CollectionUtils.isNotEmpty(purchaseItems)) {
            return purchaseItems.stream()
                    .map(PurchaseItem::getQuantity)
                    .filter(Objects::nonNull)
                    .reduce(0, Integer::sum);
        }

        return nullDefault;
    }

    public BigDecimal calculateAverageValue(List<PurchaseItem> purchaseItems) {
        if(getTotalValue().intValue() > 0){
            BigDecimal totalValue = getTotalValue();
            Integer quantity = calculateQuantity(purchaseItems, 1);
            return Optional.ofNullable(totalValue).orElse(calculateTotalValue(purchaseItems, BigDecimal.ZERO))
                    .divide(BigDecimal.valueOf(quantity), AppConstants.DEFAULT_SCALE, RoundingMode.UP);
        }

        return BigDecimal.ZERO;

    }


    public static boolean hasItemNotDistributed(List<PurchaseItem> purchaseItems) {
        if(CollectionUtils.isNotEmpty(purchaseItems)) {
            return purchaseItems.stream()
                    .map(PurchaseItem::getValueCharged)
                    .anyMatch(Objects::isNull);
        }

        return false;

    }


    public boolean hasQuantityToDistributed() {
        if(CollectionUtils.isNotEmpty(itens)) {
            return itens.stream()
                .anyMatch(PurchaseItem::hasQuantityToDistributed);
        }

        return false;
    }

}