package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "sistema")
public class Systems extends BaseEntity {

	private static final long serialVersionUID = -5428028612532165291L;

	@Id
	@SequenceGenerator(name = "system-sequence", sequenceName = "sistema_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system-sequence")
	private Long id;
	
	@NotBlank(message = "Nome is required")
	@Column(name = "nome")
	private String name;

	@ManyToMany
	@JoinTable(name = "endereco_loja",
			joinColumns = { @JoinColumn(name = "sistema_id") },
			inverseJoinColumns = { @JoinColumn(name = "endereco_id") })
	private List<Address> addresses;

}