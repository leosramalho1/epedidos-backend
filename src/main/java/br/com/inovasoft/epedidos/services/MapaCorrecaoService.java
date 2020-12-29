package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.Order;
import br.com.inovasoft.epedidos.models.entities.OrderItem;
import br.com.inovasoft.epedidos.models.entities.views.*;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class MapaCorrecaoService extends BaseService<MapaCorrecao> {

    @Inject
    TokenService tokenService;

    protected int limitPerPage = 50;

    public PaginationDataResponse<MapaCorrecao> listAll(Integer page, @NotNull Optional<Long> category) {

        Long systemId = tokenService.getSystemId();
        PanacheQuery<MapaCorrecao> list = MapaCorrecao.find("systemId", systemId);

        List<MapaCorrecao> dataList = list.page(Page.of(page - 1, limitPerPage)).list();

        if(category.isPresent()) {
            CategoriaCorrecao categoriaFiltro = CategoriaCorrecao.builder().id(category.get()).build();
            dataList = dataList.stream()
                    .filter(d -> d.getProdutosCorrecao().getCategorias().contains(categoriaFiltro))
                    .collect(Collectors.toList());
        }

        return new PaginationDataResponse<>(dataList, limitPerPage, (int) MapaCorrecao.count("systemId", systemId));
    }

    public PaginationDataResponse<MapaCorrecao> update(List<ProdutoCorrecao> produtosCorrecao) {

        produtosCorrecao.stream()
                .filter(ProdutoCorrecao::isChangedOrhasCustomerChanged)
                .forEach( produtoCorrecao -> {

                    produtoCorrecao.getClientes()
                            .forEach(clienteCorrecao -> {

                                List<PedidoCorrecao> pedidosCliente = produtoCorrecao.pedidosByCliente(clienteCorrecao);
                                PedidoCorrecao pedidoCorrecao = pedidosCliente.remove(0);

                                OrderItem orderItem = changeOrderItem(pedidoCorrecao.getId(), clienteCorrecao.getTotalPedido(), produtoCorrecao, clienteCorrecao);
                                List<OrderItem> orderItems = pedidosCliente.stream()
                                        .map(pedido -> changeOrderItem(pedido.getId(), 0, produtoCorrecao, clienteCorrecao))
                                        .collect(Collectors.toList());
                                orderItems.add(orderItem);

                                if (clienteCorrecao.isChanged()) {
                                    Order order = orderItem.getOrder();
                                    BigDecimal value = orderItems.stream().map(OrderItem::getTotalValue)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    order.setTotalProductsRealized(orderItem.getRealizedAmount());
                                    order.setTotalValueProductsRealized(value);
                                    order.setTotalLiquidProductsRealized(value);
                                    order.setStatus(OrderEnum.FINISHED);
                                    order.persist();

                                }
                            });
                });

        return listAll(1, Optional.empty());
    }

    public OrderItem changeOrderItem(Long id, Integer realizedAmount, ProdutoCorrecao produtoCorrecao, ClienteCorrecao clienteCorrecao) {
        OrderItem orderItem = OrderItem.find("id", id).firstResult();
        if(clienteCorrecao.isChanged()) {
            orderItem.setRealizedAmount(realizedAmount);
        }
        if(produtoCorrecao.isChanged()) {
            orderItem.setWeidth(produtoCorrecao.getPeso());
        }
        orderItem.setTotalValue(orderItem.getUnitValue().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        orderItem.persist();
        return orderItem;
    }

}