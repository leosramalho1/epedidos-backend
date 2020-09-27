package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Table(name = "usuario_admin", indexes = { @Index(name = "usuario_admin_index_email", columnList = "email", unique = true) })
public class UserAdmin extends BaseEntity {

	private static final long serialVersionUID = -8293624769184317958L;

	@Id
	@SequenceGenerator(name = "user-admin-sequence", sequenceName = "usuario_admin_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user-admin-sequence")
	private Long id;

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

	public UserAdmin(String name, String password, String email, LocalDateTime createOn) {
		this.name = name;
		this.password = password;
		this.email = email;
		setCreatedOn(createOn);
	}

}
