package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CNPJ;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "empresa")
public class Company extends BaseEntity {

	private static final long serialVersionUID = 6966510198943398801L;

	@Id
	@SequenceGenerator(name = "company-sequence", sequenceName = "empresa_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company-sequence")
	private Long id;

	@NotBlank(message = "Nome fantasia é obrigatório.")
	@Column(name = "nome_fantasia")
	private String name;

	@NotBlank(message = "Razão Social é obrigatório.")
	@Column(name = "nome_razao_social")
	private String business_name;

	@CNPJ(message = "CNPJ inválido.")
	@NotBlank(message = "CNPJ é obrigatório.")
	@Column(name = "cnpj")
	private String cnpj;

	@NotBlank(message = "E-mail é obrigatório.")
	@Column(name = "email")
	private String email;

	@NotBlank(message = "Telefone é obrigatório.")
	@Column(name = "telefone")
	private String telefone;
}