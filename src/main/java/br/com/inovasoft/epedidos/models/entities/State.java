package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "estado")
@Immutable
public class State extends BaseEntity {

    private static final long serialVersionUID = -2266661318852020226L;

    @Id
    @SequenceGenerator(name = "state-sequence", sequenceName = "estado_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state-sequence")
    private Long id;

    @Column(name = "nome")
    private String name;

    @NaturalId
    @Column(name = "codigo")
    private Long code;

    @Column(name = "sigla")
    private String initials;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

}