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
@Table(name = "estoque")
public class Stock extends BaseEntity {

    private static final long serialVersionUID = -2830403929042683966L;

    @Id
    @SequenceGenerator(name = "stock-sequence", sequenceName = "estoque_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock-sequence")
    private Long id;

    @NotNull(message = "Quantity is required")
    @Column(name = "quantidade")
    private Long quantity;

    @JoinColumn(name = "sistema_id")
    @ManyToOne(targetEntity = CompanySystem.class)
    private CompanySystem system;

    @JoinColumn(name = "produto_id")
    @OneToOne(targetEntity = Product.class)
    private Product product;

}