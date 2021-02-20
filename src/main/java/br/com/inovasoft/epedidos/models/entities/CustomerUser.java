package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.constraint.CpfCnpj;
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
@Table(name = "cliente_usuario", indexes = {
        @Index(name = "cliente_usuario_index_cliente", columnList = "cliente_id") })
public class CustomerUser extends BaseEntity {

    private static final long serialVersionUID = -551283431015047324L;

    @Id
    @SequenceGenerator(name = "customer-User-sequence", sequenceName = "cliente_usuario_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer-User-sequence")
    private Long id;

    @ManyToOne(targetEntity = Customer.class)
    @JoinColumn(name = "cliente_id")
    private Customer customer;

    @NotNull
    @Column(name = "nome")
    private String name;

    @NotNull
    @CpfCnpj
    @Column(name = "cpfCnpj")
    private String cpfCnpj;

    @NotNull
    @Column(name = "telefone")
    private String phone;

    @NotNull
    @Column(name = "senha")
    private String password;

    @NotNull
    @Column(name = "email")
    private String email;
}