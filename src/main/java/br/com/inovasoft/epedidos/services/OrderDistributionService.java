package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.*;
import br.com.inovasoft.epedidos.models.entities.views.*;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.models.enums.PurchaseEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class OrderDistributionService extends BaseService<OrderDistributionMap> {

    @Inject
    TokenService tokenService;

    protected int limitPerPage = 50;

    public PaginationDataResponse<OrderDistributionMap> listAll(Integer page, @NotNull Optional<Long> category) {

        Long systemId = tokenService.getSystemId();
        PanacheQuery<OrderDistributionMap> list = OrderDistributionMap.find("systemId", Sort.by("id").descending(), systemId);

        List<OrderDistributionMap> dataList = list.page(Page.of(page - 1, limitPerPage)).list();

        if(category.isPresent()) {
            ProductCategoryMap categoriaFiltro = ProductCategoryMap.builder().id(category.get()).build();
            dataList = dataList.stream()
                    .filter(d -> d.getProductMap().getCategoryMaps().contains(categoriaFiltro))
                    .collect(Collectors.toList());
        }

        return new PaginationDataResponse<>(dataList, limitPerPage, (int) OrderDistributionMap.count("systemId", systemId));
    }

    public void update(List<ProductMap> produtosCorrecao) {

        produtosCorrecao
                .forEach(productMap -> {
                    productMap.getCustomerMaps()
                            .forEach(clienteCorrecao -> {

                                List<ProductOrderItemCustomerMap> pedidosCliente = productMap.pedidosByCliente(clienteCorrecao);
                                ProductOrderItemCustomerMap orderMap = pedidosCliente.remove(0);
                                // Adiciona o valor atualizado no primeiro pedido e zera os valores nos demais pedidos
                                OrderItem orderItemMaster = changeOrderItem(orderMap.getId(),
                                        clienteCorrecao.getTotalQuantity(), productMap);
                                // Zera os valores realizados nos demais pedidos
                                pedidosCliente.forEach(pedido -> OrderItem.deleteById(pedido.getId()));

                                productMap.getPurchaseMaps()
                                        .forEach(compra -> registryPurchaseDistribution(productMap, orderItemMaster, compra));

                                Order order = orderItemMaster.getOrder();
                                order.setStatus(OrderEnum.DISTRIBUTED);
                                order.persistAndFlush();

                            });
                });

    }

    private void registryPurchaseDistribution(ProductMap productMap, OrderItem orderItemMaster, ProductPurchaseMap compra) {
        // Verifica se há itens para faturar no pedido atual
        if(orderItemMaster.hasQuantityToBilled()) {
            PurchaseItem purchaseItem = PurchaseItem
                    .find("purchase.id = ?1 and product.id = ?2 " +
                                    "and distributedQuantity < quantity "
//                                   + "and purchase.status = ?3"
                            ,
                            compra.getId(), productMap.getId()).firstResult();

            // Verifica se existe compra disponível para o produto atual.
            if (purchaseItem != null) {
                PurchaseDistribution
                        .delete("purchaseItem.id = ?1 and orderItem.id = ?2",
                                purchaseItem.getId(), orderItemMaster.getId());

                Integer avaliableQuantity = purchaseItem.calculateAvaliableQuantity();
                Integer remainingQuantity = orderItemMaster.calculateRemainingQuantity();
                Integer distributedQuantity;

                if (remainingQuantity >= avaliableQuantity) {
                    distributedQuantity = avaliableQuantity;
                } else {
                    distributedQuantity = remainingQuantity;
                }

                PurchaseDistribution purchaseDistribution = PurchaseDistribution.builder()
                        .systemId(tokenService.getSystemId())
                        .purchaseItem(purchaseItem)
                        .orderItem(orderItemMaster)
                        .customer(orderItemMaster.getOrder().getCustomer())
                        .status(OrderEnum.OPEN)
                        .quantity(distributedQuantity)
                        .valueCharged(purchaseItem.getValueCharged())
                        .build();

                Purchase purchase = purchaseItem.getPurchase();
                purchase.setStatus(PurchaseEnum.DISTRIBUTED);

                orderItemMaster.addBilledQuantity(distributedQuantity);
                purchaseItem.addDistributedQuantity(distributedQuantity);

                orderItemMaster.persist();
                purchaseItem.persist();
                purchaseDistribution.persist();
                purchase.persist();

            }
        }
    }

    public OrderItem changeOrderItem(Long id, Integer realizedAmount, ProductMap productMap) {
        OrderItem orderItem = OrderItem.findById(id);
        orderItem.setRealizedAmount(realizedAmount);
        orderItem.setWeidth(productMap.getWeidth());
        orderItem.persist();
        return orderItem;
    }

}