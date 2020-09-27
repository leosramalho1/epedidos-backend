package br.com.inovasoft.epedidos.models.entities;

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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "fornecedor", indexes = { @Index(name = "fornecedor_index_loja", columnList = "sistema_id"),
        @Index(name = "fornecedor_index_cpfCnpj", columnList = "cpfCnpj", unique = true),
        @Index(name = "fornecedor_index_email", columnList = "email", unique = true) })
public class Supplier extends BaseEntity {

    private static final long serialVersionUID = 90464850752358765L;

    @Id
    @SequenceGenerator(name = "supplier-sequence", sequenceName = "fornecedor_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier-sequence")
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "nome")
    private String name;

    @Size(max = 14)
    @NotBlank(message = "CPF/CNPJ is required")
    @Column(name = "cpfCnpj")
    private String cpfCnpj;

    @Email
    @NotBlank(message = "E-mail is required")
    @Column(name = "email")
    private String email;

    @Column(name = "contato")
    private String contactName;

    @Size(max = 20)
    @NotBlank(message = "Telefone is required")
    @Column(name = "telefone")
    private String phone;

    @NotNull(message = "Status is required")
    @Column(name = "situacao")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "sistema_id")
    private Long systemId;

}