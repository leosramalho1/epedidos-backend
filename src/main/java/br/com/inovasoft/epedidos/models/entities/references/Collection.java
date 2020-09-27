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
@Table(name = "colecao")
public class Collection extends BaseEntity {

    private static final long serialVersionUID = -4833640418584407369L;

    @Id
    @SequenceGenerator(name = "collection-sequence", sequenceName = "colecao_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "collection-sequence")
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "sistema_id")
    private Long systemId;

}