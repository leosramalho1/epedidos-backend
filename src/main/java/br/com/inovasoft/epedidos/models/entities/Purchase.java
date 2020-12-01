package br.com.inovasoft.epedidos.models.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "compra")
public class Purchase extends BaseEntity {

    private static final long serialVersionUID = 7699908322410433370L;

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
    private BigDecimal totalValueProducts;

    @NotNull
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private OrderEnum status;
}