package br.com.inovasoft.epedidos.models.entities.views;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClienteCorrecao implements Comparable<ClienteCorrecao> {

    private Long id;
    private String nome;
    @Setter(onMethod_ = @JsonProperty("total_pedido"))
    @Getter(onMethod_ = @JsonProperty("totalPedido"))
    private Integer totalPedido;
    private boolean changed;

    @Override
    public int compareTo(ClienteCorrecao o) {
        if(o == null) {
            return -1;
        }

        return Comparator.comparing(ClienteCorrecao::getNome).compare(this, o);
    }
}
