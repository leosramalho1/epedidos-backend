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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class OrderMapService extends BaseService<OrderDistributionMap> {

    @Inject
    TokenService tokenService;

    protected int limitPerPage = 50;

    public PaginationDataResponse<OrderDistributionMap> listAllDistributions(Integer page, @NotNull Optional<Long> category) {

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


    public void update2(List<ProductMap> productsMap) {

        productsMap
                .forEach(productMap -> {
                    productMap.getCustomerMaps()
                            .forEach(customer -> {

                                List<ProductOrderItemCustomerMap> orderItemsCustomer = productMap.pedidosByCliente(customer);
                                ProductOrderItemCustomerMap productOrderMap = orderItemsCustomer.remove(0);
                                // Adiciona o valor atualizado no primeiro pedido e zera os valores nos demais pedidos
                                changeOrderItem(productOrderMap.getId(), customer.getTotalQuantity(), productMap);
                                // Zera os valores realizados nos demais pedidos
                                orderItemsCustomer.forEach(orderItem -> OrderItem.deleteById(orderItem.getId()));

                            });
                });

    }
    public void update(List<ProductMap> productsMap) {

        productsMap
                .forEach(productMap -> {
                    productMap.getCustomerMaps()
                            .forEach(customer -> {

                                List<ProductOrderItemCustomerMap> orderItemsCustomer = productMap.pedidosByCliente(customer);
                                ProductOrderItemCustomerMap orderMap = orderItemsCustomer.remove(0);
                                // Adiciona o valor atualizado no primeiro pedido e zera os valores nos demais pedidos
                                OrderItem orderItemMaster = changeOrderItem(orderMap.getId(),
                                    customer.getTotalQuantity(), productMap);
                                // Zera os valores realizados nos demais pedidos
                                orderItemsCustomer.forEach(orderItem -> OrderItem.deleteById(orderItem.getId()));

                                productMap.getPurchaseMaps()
                                        .forEach(purchase -> registryPurchaseDistribution(productMap, orderItemMaster, purchase));

                                Order order = orderItemMaster.getOrder();
                                order.setStatus(OrderEnum.DISTRIBUTED);
                                order.persistAndFlush();

                            });
                });

    }

    private void registryPurchaseDistribution(ProductMap productMap, OrderItem orderItemMaster, ProductPurchaseMap productPurchase) {
        // Verifica se há itens para faturar no pedido atual
        if(orderItemMaster.hasQuantityToBilled()) {
            PurchaseItem purchaseItem = PurchaseItem
                    .find("purchase.id = ?1 and product.id = ?2 " +
                                    "and distributedQuantity < quantity "
//                                   + "and purchase.status = ?3"
                            ,
                            productPurchase.getId(), productMap.getId()).firstResult();

            // Verifica se existe compra disponível para o produto atual.
            if (purchaseItem != null) {
                PurchaseDistribution
                        .delete("purchaseItem.id = ?1 and orderItem.id = ?2",
                                purchaseItem.getId(), orderItemMaster.getId());

                Integer availableQuantity = purchaseItem.calculateAvailableQuantity();
                Integer remainingQuantity = orderItemMaster.calculateRemainingQuantity();
                Integer distributedQuantity;

                if (remainingQuantity >= availableQuantity) {
                    distributedQuantity = availableQuantity;
                } else {
                    distributedQuantity = remainingQuantity;
                }

                Customer customer = orderItemMaster.getOrder().getCustomer();
                PurchaseDistribution purchaseDistribution = PurchaseDistribution.builder()
                        .systemId(tokenService.getSystemId())
                        .purchaseItem(purchaseItem)
                        .orderItem(orderItemMaster)
                        .customer(customer)
                        .quantity(distributedQuantity)
                        .valueCharged(purchaseItem.getValueCharged())
                        .unitCustomerCost(customer.getPayValue())
                        .customerPayType(customer.getPayType())
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