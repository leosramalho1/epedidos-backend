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
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.RoleEnum;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
	@Column(name = "sistema_id")
	private Long systemId;

	@NotBlank(message = "Name is required")
	@Column(name = "nome")
	private String name;

	@NotBlank(message = "Password is required")
	@Column(name = "senha")
	private String password;

	@Email
	@NotBlank(message = "E-mail is required")
	private String email;

	@Column(name = "situacao")
	@Enumerated(EnumType.STRING)
	private StatusEnum status;

	@Column(name = "perfil")
	@Enumerated(EnumType.STRING)
	private RoleEnum role;

	@Transient
	private String confirmPassword;

	@Transient
	private String token;

	public UserPortal(String name, String password, String email, Long systemId) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.systemId = systemId;
	}

}
