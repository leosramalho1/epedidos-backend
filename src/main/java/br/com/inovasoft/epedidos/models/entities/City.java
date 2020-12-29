package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "municipio")
@Immutable
public class City extends BaseEntity {

    private static final long serialVersionUID = -2266661318852020226L;

    @Id
    @SequenceGenerator(name = "city-sequence", sequenceName = "municipio_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city-sequence")
    private Long id;

    @Column(name = "codigo_ibge")
    private Long code;

    @Column(name = "nome")
    private String name;

    @Column(name = "latitude")
    private BigDecimal latitude;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "capital")
    private boolean capital;

    @NotNull
    @ManyToOne(targetEntity = State.class)
    @JoinColumn(name = "codigo_estado", referencedColumnName = "codigo")
    private State state;

}