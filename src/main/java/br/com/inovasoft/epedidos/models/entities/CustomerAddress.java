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
@Table(name = "cliente_endereco", indexes = {
        @Index(name = "cliente_endereco_index_endereco", columnList = "endereco_id"),
        @Index(name = "cliente_endereco_index_cliente", columnList = "cliente_id") })
public class CustomerAddress extends BaseEntity {

    private static final long serialVersionUID = -551283431015047324L;

    @Id
    @SequenceGenerator(name = "customer-address-sequence", sequenceName = "cliente_endereco_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer-address-sequence")
    private Long id;

    @NotNull
    @ManyToOne(targetEntity = Address.class)
    @JoinColumn(name = "endereco_id")
    private Address address;

    @NotNull
    @ManyToOne(targetEntity = Customer.class)
    @JoinColumn(name = "cliente_id")
    private Customer customer;

    @NotNull
    @Column(name = "endereco_principal")
    private boolean primaryAddress;

    @NotNull
    @Column(name = "endereco_entrega")
    private boolean deliveryAddress;

}