package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "usuario_portal", indexes = { @Index(name = "usuario_portal_index_email", columnList = "email") })
public class UserPortal extends BaseEntity {

	private static final long serialVersionUID = 8391366118819091299L;

	@Id
	@SequenceGenerator(name = "user-portal-sequence", sequenceName = "usuario_portal_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user-portal-sequence")
	private Long id;

	@JsonIgnore
	@NotNull(message = "Store is required")
	@JoinColumn(name = "sistema_id")
	@ManyToOne(targetEntity = CompanySystem.class)
	private CompanySystem system;

	@NotBlank(message = "Name is required")
	@Column(name = "nome")
	private String name;

	@NotBlank(message = "Password is required")
	@Column(name = "senha")
	private String password;

	@Email
	@NotBlank(message = "E-mail is required")
	private String email;

	@Transient
	private String confirmPassword;

	@Transient
	private String token;

	public UserPortal(String name, String password, String email, CompanySystem system) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.system = system;
	}

}
