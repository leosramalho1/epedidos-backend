package br.com.inovasoft.epedidos.models.entities;

import br.com.inovasoft.epedidos.models.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

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

    @Column(name = "complemento")
    private String complement;

    @NotBlank
    @Column(name = "bairro")
    private String district;

    @NotNull
    @ManyToOne(targetEntity = City.class)
    @JoinColumn(name = "cidade_id")
    private City city;

    @NotNull
    @Column(name = "cep")
    private String zipcode;

    @PrePersist
    public void validate() {
        if (StringUtils.length(zipcode) != 8) {
            throw new WebApplicationException(Response.status(403).entity("CEP inválido!").build());
        }
    }

}