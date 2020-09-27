package br.com.inovasoft.epedidos.models.entities.references;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "municipio")
public class City extends BaseEntity {

    private static final long serialVersionUID = -2266661318852020226L;

    @Id
    @SequenceGenerator(name = "city-sequence", sequenceName = "municipio_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city-sequence")
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "uf")
    private String uf;
}