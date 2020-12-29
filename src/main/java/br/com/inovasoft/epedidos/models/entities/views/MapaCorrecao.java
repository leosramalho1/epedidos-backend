package br.com.inovasoft.epedidos.models.entities.views;

import br.com.inovasoft.epedidos.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "mapa_correcao")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapaCorrecao extends BaseEntity {

    private static final long serialVersionUID = 4219610476537959071L;

    @Id
    @JsonIgnore
    private Long id;

    @JsonIgnore
    @Column(name = "sistema_id")
    private Long systemId;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "mapa")
    private ProdutoCorrecao produtosCorrecao;

}