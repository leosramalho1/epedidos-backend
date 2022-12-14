package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.entities.*;
import br.com.inovasoft.epedidos.models.entities.views.*;
import br.com.inovasoft.epedidos.models.enums.CustomerPayTypeEnum;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.models.enums.PurchaseEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class OrderMapService extends BaseService<OrderDistributionMap> {

    @Inject
    TokenService tokenService;

    protected int limitPerPage = 500;

    public PaginationDataResponse<ProductMap> listAllDistributions(Integer page, @NotNull Optional<Long> category) {

        Long systemId = tokenService.getSystemId();
        PanacheQuery<OrderDistributionMap> list = OrderDistributionMap.find("systemId", Sort.by("name"), systemId);

        List<OrderDistributionMap> dataList = list.page(Page.of(page - 1, limitPerPage)).list();

        if(category.isPresent()) {
            ProductCategoryMap categoriaFiltro = ProductCategoryMap.builder().id(category.get()).build();
            dataList = dataList.stream()
                    .filter(d -> d.getProductMap().getCategoryMaps().contains(categoriaFiltro))
                    .collect(Collectors.toList());
        }

        List<ProductMap> productMapList = dataList.stream()
                .map(OrderDistributionMap::getProductMap)
                .collect(Collectors.toList());

        return new PaginationDataResponse<>(productMapList, limitPerPage,
                (int) OrderDistributionMap.count("systemId", systemId)
        );
    }

    @Transactional
    public void update(List<ProductMap> productsMap) {

        productsMap
                .forEach(productMap -> productMap.getCustomerMaps()
                        .forEach(customer -> {

                            List<ProductOrderItemCustomerMap> orderItemsCustomer = productMap.pedidosByCliente(customer);
                            ProductOrderItemCustomerMap orderMap = orderItemsCustomer.remove(0);
                            // Adiciona o valor atualizado no primeiro pedido e zera os valores nos demais pedidos
                            OrderItem orderItemMaster = changeOrderItem(orderMap.getId(), customer.getTotalDistributed(), orderItemsCustomer);
                            // Zera os valores realizados nos demais pedidos
                            orderItemsCustomer.forEach(orderItem -> OrderItem.deleteById(orderItem.getId()));

                            if (!Objects.isNull(orderItemMaster)) {
                                productMap.getPurchaseMaps()
                                        .forEach(purchase -> registryPurchaseDistribution(productMap, orderItemMaster, purchase));

                                Order order = orderItemMaster.getOrder();
                                order.setStatus(OrderEnum.DISTRIBUTED);
                                order.persistAndFlush();

                            }

                        }));

    }

    private void registryPurchaseDistribution(ProductMap productMap, OrderItem orderItemMaster, ProductPurchaseMap productPurchase) {
        // Verifica se h?? itens para faturar no pedido atual
        if(!Objects.isNull(orderItemMaster) && orderItemMaster.hasQuantityToBilled()) {
            PurchaseItem purchaseItem = PurchaseItem
                    .find("purchase.id = ?1 and product.id = ?2 " +
                                    "and (distributedQuantity is null or distributedQuantity < quantity) " +
                                "and packageType = ?3 and weight = ?4"
                            ,
                            productPurchase.getId(), productMap.getId(),
                            productMap.getPackageType(), productMap.getWeidth())
                    .firstResult();

            // Verifica se existe compra dispon??vel para o produto atual.
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
                Product product = orderItemMaster.getProduct();

                BigDecimal unitCustomerCost = customer.getPayValue();
                CustomerPayTypeEnum payType = customer.getPayType();

                if(payType == CustomerPayTypeEnum.V && Objects.nonNull(product.getPayValue())) {
                    unitCustomerCost = product.getPayValue();
                }

                PurchaseDistribution purchaseDistribution = PurchaseDistribution.builder()
                        .systemId(tokenService.getSystemId())
                        .purchaseItem(purchaseItem)
                        .orderItem(orderItemMaster)
                        .customer(customer)
                        .quantity(distributedQuantity)
                        .valueCharged(purchaseItem.getValueCharged())
                        .unitCustomerCost(unitCustomerCost)
                        .customerPayType(payType)
                        .packageType(purchaseItem.getPackageType())
                        .product(product)
                        .unitShippingCost(product.getShippingCost())
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

    public OrderItem changeOrderItem(Long id, Integer realizedAmount,  List<ProductOrderItemCustomerMap> items) {
        OrderItem orderItem = OrderItem.find("id = ?1", id).firstResult();
        if(!Objects.isNull(orderItem)) {
            if(CollectionUtils.isNotEmpty(items)) {
                // Unifica os pedidos do cliente na base de acordo com o produto
                List<Long> ids = items.stream().map(ProductOrderItemCustomerMap::getId).collect(Collectors.toList());
                List<OrderItem> orders = OrderItem.list("id in (?1)", ids);
                orderItem.setQuantity(orders.stream().map(OrderItem::getQuantity).reduce(0, Integer::sum));
            }
            orderItem.addRealizedAmount(realizedAmount);
            orderItem.persist();
        }

        return orderItem;
    }

}