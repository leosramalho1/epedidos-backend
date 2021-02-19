package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.enums.SetupEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "configuracao", indexes = {
		@Index(name = "configuracao_index_sistema", columnList = "sistema_id"),
		@Index(name = "configuracao_index_chave", columnList = "chave"),
		@Index(name = "configuracao_index_chave_sistema", columnList = "chave, sistema_id")
})
public class Setup extends BaseEntity {

	private static final long serialVersionUID = 6966510198943398801L;

	@Id
	@SequenceGenerator(name = "setup-sequence", sequenceName = "configuracao_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "setup-sequence")
	private Long id;

	@NotNull(message = "Chave é obrigatório.")
	@Enumerated(EnumType.STRING)
	@Column(name = "chave")
	private SetupEnum key;

	@NotBlank(message = "Valor é obrigatório.")
	@Column(name = "valor")
	private String value;

	@NotNull(message = "Sistema é obrigatório.")
	@Column(name = "sistema_id")
	private Long systemId;

}