package br.com.inovasoft.epedidos.models.entities.views;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Comparator;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClienteCorrecao implements Comparable<ClienteCorrecao> {

    private Long id;
    private String nome;
    @JsonProperty("total_pedido")
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
