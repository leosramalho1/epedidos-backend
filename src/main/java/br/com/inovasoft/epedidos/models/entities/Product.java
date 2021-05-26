package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.entities.references.Category;
import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@ToString(of = {"id", "name"})
@EqualsAndHashCode(callSuper = false)
@Table(name = "produto", indexes = {@Index(name = "produto_index_system", columnList = "sistema_id")})
@Cacheable
public class Product extends BaseEntity {

    private static final long serialVersionUID = 7561993588630057369L;

    @Id
    @SequenceGenerator(name = "product-sequence", sequenceName = "produto_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product-sequence")
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "nome")
    private String name;

    @Column(name = "peso")
    private BigDecimal weidth;

    @Column(name = "id_comprador")
    private Long buyerId;

    @Column(name = "tipo_embalagem")
    @Enumerated(EnumType.STRING)
    private PackageTypeEnum packageType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "sistema_id")
    private Long systemId;

    @ManyToMany
    @JoinTable(name = "produto_categoria", joinColumns = {
            @JoinColumn(name = "produto_id")}, inverseJoinColumns = {@JoinColumn(name = "categoria_id")})
    private List<Category> categories;

    @Column(name = "valor_frete")
    private BigDecimal shippingCost;

    @Column(name = "cobranca_valor")
    private BigDecimal payValue;

    @Column(name = "margem_lucro")
    private BigDecimal profitMargin;

}