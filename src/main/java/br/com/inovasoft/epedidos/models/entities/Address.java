package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import br.com.inovasoft.epedidos.models.entities.references.City;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "endereco")
public class Address extends BaseEntity {

    private static final long serialVersionUID = 8787805752138764133L;

    @Id
    @SequenceGenerator(name = "address-sequence", sequenceName = "endereco_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address-sequence")
    private Long id;

    @NotBlank
    @Column(name = "logradouro")
    private String street;

    @NotBlank
    @Column(name = "numero")
    private String number;

    @NotBlank
    @Column(name = "complemento")
    private String complement;

    @NotBlank
    @Column(name = "bairro")
    private String neighborhood;

    @NotNull
    @ManyToOne(targetEntity = City.class)
    @JoinColumn(name = "cidade_id")
    private City city;

    @NotBlank
    @Size(max = 8)
    @Column(name = "cep")
    private Integer zipCode;

}