package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "imagem")
public class Image extends BaseEntity {
 
    private static final long serialVersionUID = 1153794005407928000L;

    @Id
    @SequenceGenerator(name = "image-sequence", sequenceName = "imagem_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image-sequence")
    private Long id;

    @JoinColumn(name = "sistema_id")
    @ManyToOne(targetEntity = CompanySystem.class)
    private CompanySystem system;

    private String url;

    private String name;

    private String key;

}