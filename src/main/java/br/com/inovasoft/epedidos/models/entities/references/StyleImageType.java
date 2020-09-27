package br.com.inovasoft.epedidos.models.entities.references;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "estilo_imagem")
public class StyleImageType extends BaseEntity {

    private static final long serialVersionUID = 7049213412879104530L;

    @Id
    @SequenceGenerator(name = "style-image-sequence", sequenceName = "estilo_imagem_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "style-image-sequence")
    private Long id;

    @Column(name = "nome")
    private String name;
}