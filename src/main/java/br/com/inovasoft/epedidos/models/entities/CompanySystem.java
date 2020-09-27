package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
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
@Table(name = "empresa_sistema")
public class CompanySystem extends BaseEntity {

	private static final long serialVersionUID = 5534575337461337762L;

	@Id
	@SequenceGenerator(name = "company-system-sequence", sequenceName = "empresa_sistema_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company-system-sequence")
	private Long id;

	@NotNull
	@JoinColumn(name = "empresa_id")
	@ManyToOne(targetEntity = Company.class)
	private Company company;

	@NotNull
	@JoinColumn(name = "sistema_id")
	@ManyToOne(targetEntity = Systems.class)
	private Systems system;

	@Column(name = "qtd_limite_usuario")
	private Integer maxUser;

	@Column(name = "email_admin")
	private String emailAdmin;

	@Column(name = "situacao")
	private String status;

	@Column(name = "chave_sistema")
	private String systemKey;

	@Transient
	private Long idSystem;

}