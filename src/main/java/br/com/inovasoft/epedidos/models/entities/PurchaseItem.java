package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "compra_item")
public class PurchaseItem extends BaseEntity {

    private static final long serialVersionUID = 1998008985093338252L;

    @Id
    @SequenceGenerator(name = "compra-item-sequence", sequenceName = "compra_item_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "compra-item-sequence")
    private Long id;

    @NotNull(message = "Order is required")
    @JoinColumn(name = "compra_id")
    @ManyToOne(targetEntity = Purchase.class)
    private Purchase purchase;

    @NotNull(message = "Product is required")
    @JoinColumn(name = "produto_id")
    @OneToOne(targetEntity = Product.class)
    private Product product;

    private Integer quantity;

    private BigDecimal unitValue;

    private BigDecimal totalValue;

    @Enumerated(EnumType.STRING)
    private PackageTypeEnum packageType;

}