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
@Table(name = "ref_produto_imagem")
public class ProductImageType extends BaseEntity {
    
    private static final long serialVersionUID = -4335812015448396992L;

    @Id
    @SequenceGenerator(name = "product-image-sequence", sequenceName = "ref_produto_imagem_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product-image-sequence")
    private Long id;
    
    @Column(name = "nome")
    private String name;

}