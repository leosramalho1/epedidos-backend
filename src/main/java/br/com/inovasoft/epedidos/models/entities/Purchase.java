package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @NotNull
    @Column(name = "data_vencimento")
    private LocalDate dueDate;
}