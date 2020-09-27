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
@Table(name = "categoria")
public class Category extends BaseEntity {

    private static final long serialVersionUID = 4219610476537959071L;

    @Id
    @SequenceGenerator(name = "category-sequence", sequenceName = "categoria_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category-sequence")
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "sistema_id")
    private Long systemId;

}