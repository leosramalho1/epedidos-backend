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
@Table(name = "fornecedor_endereco", indexes = {
        @Index(name = "fornecedor_endereco_index_endereco", columnList = "endereco_id"),
        @Index(name = "fornecedor_endereco_index_fornecedor", columnList = "fornecedor_id") })
public class SupplierAddress extends BaseEntity {

    private static final long serialVersionUID = -5512834310047324L;

    @Id
    @SequenceGenerator(name = "supplier-address-sequence", sequenceName = "fornecedor_endereco_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier-address-sequence")
    private Long id;

    @NotNull
    @ManyToOne(targetEntity = Address.class)
    @JoinColumn(name = "endereco_id")
    private Address address;

    @NotNull
    @ManyToOne(targetEntity = Supplier.class)
    @JoinColumn(name = "fornecedor_id")
    private Supplier supplier;

    @NotNull
    @Column(name = "endereco_principal")
    private boolean primaryAddress;

    @NotNull
    @Column(name = "endereco_entrega")
    private boolean deliveryAddress;

}