package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.configuration.AppConstants;
import br.com.inovasoft.epedidos.exceptions.IllegalCustomerPayTypeException;
import br.com.inovasoft.epedidos.mappers.CustomerMapper;
import br.com.inovasoft.epedidos.mappers.PurchaseDistributionMapper;
import br.com.inovasoft.epedidos.models.dtos.BillingClosingDto;
import br.com.inovasoft.epedidos.models.dtos.CustomerBillingDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.PurchaseDistributionDto;
import br.com.inovasoft.epedidos.models.entities.*;
import br.com.inovasoft.epedidos.models.enums.CustomerPayTypeEnum;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.models.enums.PurchaseEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class PurchaseDistributionService extends BaseService<PurchaseDistribution> {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    public static final String GROUP_BY_ORDER = "ORDER";
    public static final String GROUP_BY_PRODUCT = "PRODUCT";

    @Inject
    PurchaseDistributionMapper mapper;

    @Inject
    TokenService tokenService;

    @Inject
    CustomerMapper customerMapper;


    public PaginationDataResponse<CustomerBillingDto> buildAllByCustomer(int page, Long idCustomer, OrderEnum orderEnum, String groupBy) {

        List<PurchaseDistribution> dataList = listAllUninvoiced(idCustomer, orderEnum);

        Map<Customer, List<PurchaseDistribution>> ordersByCustomer = dataList.stream()
                .collect(Collectors.groupingBy(PurchaseDistribution::getCustomer));

        List<CustomerBillingDto> collect = ordersByCustomer.entrySet().parallelStream()
                .map(i -> {
                    Customer customer = i.getKey();
                    List<PurchaseDistribution> purchaseDistributionList = i.getValue();

                    CustomerBillingDto customerBillingDto = customerMapper.toBillingClosingDto(customer);

                    BigDecimal totalValue = sumTotalValue(purchaseDistributionList);
                    Integer quantity = sumTotalQuantity(purchaseDistributionList);
                    BigDecimal shippingCost = sumShippingCost(purchaseDistributionList);
                    Map<Long, List<PurchaseDistribution>> distributions;

                    if(GROUP_BY_ORDER.equals(groupBy)) {
                        distributions = purchaseDistributionList.stream()
                                .collect(Collectors.groupingBy(pd -> pd.getOrderItem().getOrder().getId()));
                    } else {
                        distributions = purchaseDistributionList.stream()
                                .collect(Collectors.groupingBy(pd -> pd.getOrderItem().getProduct().getId()));
                    }

                    List<PurchaseDistributionDto> purchaseDistributions = mapPurchasesDistributions(distributions, groupBy);

                    return customerBillingDto.toBuilder()
                            .totalCustomerCost(calculateTotalValueProducts(purchaseDistributionList, customer))
                            .productsValue(totalValue)
                            .totalShippingCost(shippingCost)
                            .quantity(quantity)
                            .purchaseDistributions(purchaseDistributions)
                            .build();

                })
                .sorted(Comparator.comparing(CustomerBillingDto::getName))
                .collect(Collectors.toList());

        List<CustomerBillingDto> response = collect.stream()
                .skip((page - 1) * LIMIT_PER_PAGE)
                .limit(LIMIT_PER_PAGE)
                .collect(Collectors.toList());

        return new PaginationDataResponse<>(response, LIMIT_PER_PAGE, collect.size());
    }

    private List<PurchaseDistribution> listAllUninvoiced(Long idCustomer, OrderEnum orderEnum) {

        String select = "select pd ";
        String where = "from PurchaseDistribution pd " +
                "inner join pd.orderItem oi " +
                "inner join oi.order ord " +
                "where ord.deletedOn is null " +
                "and pd.customer.deletedOn is null " +
                "and pd.accountToReceive is null " +
                "and pd.systemId = :systemId " +
                "and ord.status = :status"
                ;

        Parameters parameters = Parameters.with("systemId", tokenService.getSystemId());
        parameters.and("status", orderEnum);

        if(idCustomer != null) {
            where += " and pd.customer.id = :idCustomer";
            parameters.and("idCustomer", idCustomer);
        }

        return PurchaseDistribution.find(select + where, parameters).list();
    }

    private List<PurchaseDistributionDto> mapPurchasesDistributions(Map<Long, List<PurchaseDistribution>> distributions, String groupBy) {

        return distributions.values()
                .parallelStream().map(value -> {

                    BigDecimal totalValue = sumTotalValue(value);
                    Integer quantity = sumTotalQuantity(value);
                    BigDecimal shippingCost = sumShippingCost(value);
                    PurchaseDistribution entity = value.get(0);
                    PurchaseDistributionDto purchaseDistributionDto = mapper.toDto(entity);
                    BigDecimal unitValue;

                    if (GROUP_BY_ORDER.equals(groupBy)) {
                        unitValue = sumUnitValue(value);
                    } else {
                        unitValue = sumUnitValue(value)
                                .divide(BigDecimal.valueOf(quantity), AppConstants.MONEY_SCALE, RoundingMode.UP);
                    }

                    return purchaseDistributionDto.toBuilder()
                            .quantity(quantity)
                            .totalValue(totalValue)
                            .totalCustomerCost(calculateTotalValueProducts(value, entity.getCustomer()))
                            .unitShippingCost(shippingCost)
                            .unitValue(unitValue)
                            .valueCharged(totalValue
                                    .divide(BigDecimal.valueOf(quantity), AppConstants.MONEY_SCALE, RoundingMode.UP))
                            .build();

                }).sorted(Comparator.comparing(PurchaseDistributionDto::getIdOrder)
                        .thenComparing(PurchaseDistributionDto::getNameProduct))
                .collect(Collectors.toList());
    }

    private BigDecimal sumShippingCost(List<PurchaseDistribution> purchaseDistributionList) {
        return purchaseDistributionList.stream()
                                .map(pd -> pd.getOrderItem().getUnitShippingCost()
                                        .multiply(BigDecimal.valueOf(pd.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer sumTotalQuantity(List<PurchaseDistribution> purchaseDistributionList) {
        return purchaseDistributionList.stream()
                .map(PurchaseDistribution::getQuantity)
                .reduce(0, Integer::sum);
    }

    private BigDecimal sumTotalValue(List<PurchaseDistribution> purchaseDistributionList) {
        return purchaseDistributionList.stream()
                .map(PurchaseDistribution::calculateTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumUnitValue(List<PurchaseDistribution> purchaseDistributionList) {
        return purchaseDistributionList.stream()
                .map(p -> p.getPurchaseItem().getUnitValue()
                        .multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumTotalValueCharged(List<PurchaseDistribution> purchaseDistributionList) {
        return purchaseDistributionList.stream()
                .map(PurchaseDistribution::getValueCharged)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<AccountToReceive> invoice(BillingClosingDto billingClosing) {
        List<AccountToReceive> accountToReceives = new ArrayList<>();
        List<CustomerBillingDto> items = billingClosing.getItems();

        items.forEach(customerBillingDto -> {

            AccountToReceive accountToReceive = new AccountToReceive();
            accountToReceive.setCustomer(Customer.findById(customerBillingDto.getId()));
            accountToReceive.setOriginalValue(customerBillingDto.getTotalValue());
            accountToReceive.setDueDate(billingClosing.getDueDate());
            accountToReceive.setSystemId(tokenService.getSystemId());
            accountToReceive.persist();

            List<PurchaseDistributionDto> purchaseDistributionsDto = customerBillingDto.getPurchaseDistributions();

            purchaseDistributionsDto.forEach(dto -> {

                List<PurchaseDistribution> purchaseDistributions = listAllUninvoiced(customerBillingDto.getId(),
                        OrderEnum.FINISHED);

                purchaseDistributions.forEach(entity -> {

                    OrderItem orderItem = entity.getOrderItem();
                    Order order = orderItem.getOrder();
                    if (order.getStatus() != OrderEnum.BILLED) {
                        order.setStatus(OrderEnum.BILLED);
                        order.persist();
                    }

                    PurchaseItem purchaseItem = entity.getPurchaseItem();
                    Purchase purchase = purchaseItem.getPurchase();
                    if (!purchase.hasQuantityToDistributed()
                            && purchase.getStatus() != PurchaseEnum.FINISHED) {
                        purchase.setStatus(PurchaseEnum.FINISHED);
                        purchase.persist();
                    }

                    entity.setAccountToReceive(accountToReceive);
                    entity.persistAndFlush();

                });
            });

            accountToReceives.add(accountToReceive);

        });

        return accountToReceives;

    }


    @Transactional
    public void updateValueCharged(List<PurchaseDistributionDto> purchaseDistributions) {
        purchaseDistributions.forEach(purchaseDistributionDto -> {
            PurchaseDistribution.update("update PurchaseDistribution p set p.valueCharged = ?1 " +
                            "where product.id = ?2 and customer.id = ?3 " +
                            "and systemId = ?4 and accountToReceive is null ",
                    purchaseDistributionDto.getValueCharged(),
                    purchaseDistributionDto.getIdProduct(), purchaseDistributionDto.getIdCustomer(),
                    tokenService.getSystemId());

        });
    }


    public BigDecimal calculateTotalValueProducts(List<PurchaseDistribution> purchaseDistributions, Customer customer) {
        BigDecimal totalValueProductsRealized;

        if (customer.getPayType() == CustomerPayTypeEnum.V) {
            totalValueProductsRealized = purchaseDistributions.stream()
                    .map(PurchaseDistribution::calculateCustomerValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else if (customer.getPayType() == CustomerPayTypeEnum.P) {
            totalValueProductsRealized = purchaseDistributions.stream()
                    .map(PurchaseDistribution::calculateCustomerValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            throw new IllegalCustomerPayTypeException();
        }

        return totalValueProductsRealized;
    }

}