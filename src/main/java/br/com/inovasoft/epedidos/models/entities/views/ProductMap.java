package br.com.inovasoft.epedidos.models.entities.views;

import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductMap implements Serializable {

    private static final long serialVersionUID = -6285155077553460264L;

    private Long id;
    @JsonProperty(value = "nome")
    private String name;
    @JsonProperty(value = "totalComprado")
    private Long totalPurchaseValue;
    @JsonProperty(value = "clientes")
    private SortedSet<ProductCustomerMap> customerMaps;
    @JsonProperty(value = "pedidos")
    private Set<ProductOrderItemCustomerMap> orderMaps;
    @JsonProperty(value = "categorias")
    private Set<ProductCategoryMap> categoryMaps;
    @JsonProperty(value = "totalPedido")
    private Integer totalOrderValue;
    @JsonProperty(value = "compras")
    private Set<ProductPurchaseMap> purchaseMaps;
    @JsonProperty(value = "tipoEmbalagem")
    private PackageTypeEnum packageType;
    private boolean changed;
//    @JsonProperty(value = "totalDistribuido")
//    private Map<PackageTypeEnum, Integer> totalDistributed = Map.of(PackageTypeEnum.DISPOSABLE, 0, PackageTypeEnum.RETURNABLE, 0);
    @JsonProperty(value = "totalDistribuido")
    private Integer totalDistributed = 0;
    private String _rowVariant;

    public Integer getTotalOrderValue() {

        if(CollectionUtils.isNotEmpty(customerMaps)){
            return customerMaps.stream()
                    .map(ProductCustomerMap::getTotalQuantity)
                    .reduce(0, Integer::sum);
        }

        return 0;
    }

    public boolean hasCustomerChanged() {
        return CollectionUtils.isNotEmpty(customerMaps)
                && customerMaps.stream().anyMatch(ProductCustomerMap::isChanged);
    }

    public boolean hasCustomer(ProductCustomerMap customerMap) {
        return CollectionUtils.isNotEmpty(customerMaps)
                && customerMaps.contains(customerMap);
    }

    public boolean isChangedOrhasCustomerChanged() {
        return isChanged() || hasCustomerChanged();
    }

    public List<ProductOrderItemCustomerMap> pedidosByCliente(ProductCustomerMap customerMap) {

        if(customerMap == null || customerMap.getId() == null) {
            return Collections.emptyList();
        }

        return getOrderMaps().stream()
                .filter(p -> p.getCustomer().equals(customerMap.getId()) )
                .collect(Collectors.toList());
    }
}
