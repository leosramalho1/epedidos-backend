package br.com.inovasoft.epedidos.models.entities.views;

import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoCorrecao {

    private Long id;
    private String nome;
    private BigDecimal peso;
    private Long totalComprado;
    @Setter(onMethod_ = @JsonProperty("tipo_embalagem"))
    @Getter(onMethod_ = @JsonProperty("tipoEmbalagem"))
    private PackageTypeEnum tipoEmbalagem;
    private SortedSet<ClienteCorrecao> clientes;
    private Set<PedidoCorrecao> pedidos;
    private Set<CategoriaCorrecao> categorias;
    private Integer totalPedido;
    private boolean changed;

    public Integer getTotalPedido() {

        if(CollectionUtils.isNotEmpty(clientes)){
            return clientes.stream()
                    .map(ClienteCorrecao::getTotalPedido)
                    .reduce(0, Integer::sum);
        }

        return 0;
    }

    public boolean hasCustomerChanged() {
        return CollectionUtils.isNotEmpty(clientes)
                && clientes.stream().anyMatch(ClienteCorrecao::isChanged);
    }

    public boolean isChangedOrhasCustomerChanged() {
        return isChanged() || hasCustomerChanged();
    }

    public List<PedidoCorrecao> pedidosByCliente(ClienteCorrecao clienteCorrecao) {

        if(clienteCorrecao == null || clienteCorrecao.getId() == null) {
            return Collections.emptyList();
        }

        return getPedidos().stream()
                .filter(p -> p.getCliente().equals(clienteCorrecao.getId()))
                .collect(Collectors.toList());
    }
}
