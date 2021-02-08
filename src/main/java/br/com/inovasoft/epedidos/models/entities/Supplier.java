package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import br.com.inovasoft.epedidos.util.FormatUtil;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "fornecedor", indexes = { @Index(name = "fornecedor_index_sistema", columnList = "sistema_id"),
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

    @PrePersist
    @PreUpdate
    public void formatValues() {
        this.cpfCnpj = FormatUtil.onlyNumbers(this.cpfCnpj);
        this.phone = FormatUtil.onlyNumbers(this.phone);
    }

}