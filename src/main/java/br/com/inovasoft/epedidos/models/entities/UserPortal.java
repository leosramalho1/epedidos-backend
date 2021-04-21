package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.RoleEnum;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "usuario_portal", indexes = {
		@Index(name = "usuario_portal_index_email", columnList = "email"),
		@Index(name = "usuario_portal_index_sistema", columnList = "sistema_id"),
		@Index(name = "usuario_portal_index_sistema_perfil", columnList = "sistema_id,  deletedOn"),
		@Index(name = "usuario_portal_index_sistema_perfil_nome", columnList = "sistema_id,  nome, deletedOn") })
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

	@Column(name = "administrador")
	private Boolean isAdmin;


	@Column(name = "comprador")
	private Boolean isBuyer;

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
