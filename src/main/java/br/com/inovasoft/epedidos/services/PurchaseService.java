package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.PaymentMethodMapper;
import br.com.inovasoft.epedidos.mappers.PurchaseAppMapper;
import br.com.inovasoft.epedidos.mappers.PurchaseItemMapper;
import br.com.inovasoft.epedidos.mappers.PurchaseMapper;
import br.com.inovasoft.epedidos.models.dtos.*;
import br.com.inovasoft.epedidos.models.entities.*;
import br.com.inovasoft.epedidos.models.entities.references.PaymentMethod;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.models.enums.PurchaseEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class PurchaseService extends BaseService<Purchase> {

    @Inject
    TokenService tokenService;

    @Inject
    PurchaseMapper mapper;

    @Inject
    PurchaseAppMapper purchaseAppMapper;

    @Inject
    PurchaseItemMapper purchaseItemMapper;

    @Inject
    PaymentMethodMapper paymentMethodMapper;

    @Inject
    PackageLoanService packageLoanService;

    public PaginationDataResponse<PurchaseDto> listAll(int page, Long supplier, Long buyer) {
        Parameters parameters = Parameters.with("systemId", tokenService.getSystemId());
        String query = "systemId = :systemId and deletedOn is null";

        if (supplier != null) {
            query += " and supplier.id = :supplier";
            parameters.and("supplier", supplier);
        }

        if (buyer != null) {
            query += " and buyer.id = :buyer";
            parameters.and("buyer", buyer);
        }

        PanacheQuery<Purchase> listPurchases = Purchase.find(query, Sort.by("id").descending(), parameters);

        List<Purchase> dataList = listPurchases.page(Page.of(page - 1, LIMIT_PER_PAGE)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), LIMIT_PER_PAGE,
                (int) Purchase.count(query, parameters));
    }

    public PaginationDataResponse<PurchaseItemDto> listMap(int page, Long supplier, String nameProduct) {

        String select = "select new PurchaseItem(pr.id, sum(pi.quantity), " +
                "sum(pi.unitValue * pi.quantity), " +
                "sum(pi.unitValue * pi.quantity) / sum(pi.quantity) as avgUnitValue, " +
                "sum(COALESCE(pi.valueCharged, pi.unitValue) * pi.quantity) as sumValueCharged," +
                "pi.weight) %s";
        String where = "from PurchaseItem pi join pi.purchase p join pi.product pr " +
                "where p.systemId = :systemId " +
                "and p.status in (:status) " +
                "and p.deletedOn is null " +
                "and pr.deletedOn is null ";

        Parameters parameters = Parameters.with("systemId", tokenService.getSystemId());
        parameters.and("status", List.of(PurchaseEnum.OPEN));

        if (supplier != null) {
            where += " and p.supplier.id = :supplier ";
            parameters.and("supplier", supplier);
        }

        if (StringUtils.isNotBlank(nameProduct)) {
            where += " and upper(pr.name) like :nameProduct ";
            parameters.and("nameProduct", "%" + nameProduct.toUpperCase() + "%");
        }

        PanacheQuery<PurchaseItem> list = PurchaseItem.find(
                String.format(select, where + " group by pr.id, pi.weight "),
                Sort.by("pr.name"), parameters
        );

        List<PurchaseItem> dataList = list.list();

        int limitPerPage = 50;

        List<PurchaseItem> response = dataList.stream()
                .skip((page - 1) * limitPerPage)
                .limit(limitPerPage)
                .collect(Collectors.toList());

        return new PaginationDataResponse<>(purchaseItemMapper.toDto(response), limitPerPage,  Math.max(dataList.size(), 0));
    }

    @Transactional
    public List<PurchaseItemDto> saveMap(List<PurchaseItemDto> purchaseItemDtos) {

        purchaseItemDtos.forEach(purchaseItemDto -> {
            Long idProduct = purchaseItemDto.getIdProduct();
            BigDecimal weight = purchaseItemDto.getWeight();
            List<PurchaseItem> purchaseItems = PurchaseItem.list("product.id = ?1 " +
                            "and purchase.systemId = ?2 and purchase.status in (?3) and weight = ?4",
                    idProduct, tokenService.getSystemId(), List.of(PurchaseEnum.OPEN), weight);

            purchaseItems.forEach(purchaseItem -> {
                purchaseItem.setValueCharged(purchaseItemDto.getValueCharged());
                purchaseItem.persist();

                Purchase purchase = purchaseItem.getPurchase();
                purchase.prePersist();
                purchase.persist();

            });

            List<PurchaseDistribution> purchaseDistributions = PurchaseDistribution
                    .list("select new PurchaseDistribution(pd.id) " +
                            "from PurchaseDistribution pd " +
                            "join pd.purchaseItem pi " +
                            "where pi.product.id = ?1 " +
                            "and pd.accountToReceive.id is null", idProduct);

            List<Long> ids = purchaseDistributions.stream()
                    .map(PurchaseDistribution::getId)
                    .collect(Collectors.toList());

            PurchaseDistribution
                    .update("valueCharged = ?1 " +
                                    "where id in (?2)",
                            purchaseItemDto.getValueCharged(), ids);


        });

        return purchaseItemDtos;
    }


    public List<PurchaseAppDto> listPurchasesByBuyer() {
        PanacheQuery<Purchase> listPurchases = Purchase.find(" select new Purchase(id, supplier.id, " +
                        "supplier.name, createdOn, totalValue ) from Purchase " +
                        "where buyer.email = ?1 and deletedOn is null",
                tokenService.getUserEmail());

        return purchaseAppMapper.to(mapper.toDto(listPurchases.list()));
    }

    public Purchase findById(Long id) {
        return Purchase.find("id = ?1 and systemId = ?2 and deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public PurchaseGroupDto getOpenOrderAndGroupByIdBuyer(Long buyerId) {
        LocalDate yesterday = LocalDate.now();

        List<OrderItem> ordersItems = OrderItem.list(
                "select oi from OrderItem oi where oi.order.status=?1 " +
                        "and oi.product.buyerId= ?2 " +
                        "and oi.order.deletedOn is null order by oi.product.id",
                OrderEnum.PURCHASE, buyerId);

        List<PurchaseItem> purchaseItems = PurchaseItem.list(
                "select pi from PurchaseItem pi where pi.purchase.deletedOn is null and pi.purchase.status=?1 " +
                        "and pi.purchase.buyer.id=?2 " +
                        "order by pi.product.id",
                PurchaseEnum.OPEN, buyerId);

        return mountPurchaseGroup(buyerId, yesterday, ordersItems, purchaseItems);
    }

    public PurchaseDto getProductsToBuy(Long buyerId) {
        List<Product> products = Product.list("buyerId = ?1", Sort.by("name"), buyerId);

        PurchaseGroupDto itensToBuy = getOpenOrderAndGroupByIdBuyer(buyerId);
        
        final Map<Long, Integer> productToBuyMap = new HashMap<Long, Integer>();
        if(itensToBuy != null && itensToBuy.getItens() != null){
            productToBuyMap.putAll(itensToBuy.getItens().stream()
            .collect(Collectors.toMap(PurchaseItemDto::getIdProduct, item -> item.getQuantity())));
        }
        

        List<PurchaseItemDto> items = products.stream()
                .map(product -> new PurchaseItemDto(product.getId(), product.getName(), 0, productToBuyMap.get(product.getId()),product.getPackageType(), false, product.getWeidth()))
                .collect(Collectors.toList());

        PurchaseDto result = new PurchaseDto();
        result.setItens(items);
        return result;
    }

    private PurchaseGroupDto mountPurchaseGroup(Long buyerId, LocalDate refDate, List<OrderItem> ordersItems,
            List<PurchaseItem> purchaseItems) {
        UserPortal buyer = UserPortal.findById(buyerId);
        PurchaseGroupDto purchaseGroup = new PurchaseGroupDto();
        purchaseGroup.setBuyer(new UserPortalDto());
        purchaseGroup.getBuyer().setId(buyerId);
        purchaseGroup.getBuyer().setName(buyer.getName());
        purchaseGroup.setDateRef(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(refDate));

        Map<Long, PurchaseItemDto> map = new HashMap<>();

        if (CollectionUtils.isNotEmpty(ordersItems)) {
            for (OrderItem item : ordersItems) {
                PurchaseItemDto purchaseItemDto = map.get(item.getProduct().getId());
                if (purchaseItemDto == null) {
                    purchaseItemDto = new PurchaseItemDto();
                    purchaseItemDto.setIdProduct(item.getProduct().getId());
                    purchaseItemDto.setNameProduct(item.getProduct().getName());
                    purchaseItemDto.setQuantity(item.getQuantity());
                    purchaseItemDto.setUnitValue(BigDecimal.ZERO);
                    purchaseItemDto.setTotalValue(BigDecimal.ZERO);

                    map.put(item.getProduct().getId(), purchaseItemDto);
                } else {
                    purchaseItemDto.setQuantity(item.getQuantity() + purchaseItemDto.getQuantity());
                }
            }
        }

        if (CollectionUtils.isNotEmpty(purchaseItems) && MapUtils.isNotEmpty(map)) {
            for (PurchaseItem purchaseItem : purchaseItems) {
                PurchaseItemDto purchaseItemDto = map.get(purchaseItem.getProduct().getId());
                purchaseItemDto.setQuantity(purchaseItemDto.getQuantity() - purchaseItem.getQuantity());
            }

        }

        List<PurchaseItemDto> items = new ArrayList<>(map.values());
        purchaseGroup.setItens(items.stream().filter(item -> item.getQuantity() > 0).collect(Collectors.toList()));

        items.sort(Comparator.comparing(PurchaseItemDto::getNameProduct, Comparator.naturalOrder()))    ;

        return purchaseGroup;
    }

    public PurchaseAppDto findAppDtoById(Long id) {
        Purchase entity = findById(id);

        PurchaseDto purchase = mapper.toDto(entity);

        purchase.setItens(purchaseItemMapper
                .toDto(PurchaseItem.list("purchase.id = ?1 order by product.name", purchase.getId())));

        return purchaseAppMapper.to(purchase);
    }

    public PurchaseDto findDtoById(Long id) {
        Purchase entity = findById(id);

        PurchaseDto purchase = mapper.toDto(entity);

        purchase.setItens(purchaseItemMapper
                .toDto(PurchaseItem.list("purchase.id = ?1 order by product.name", purchase.getId())));
        if(purchase.getPaymentMethod() != null) {
            PaymentMethod paymentMethod = (PaymentMethod) PaymentMethod.find("id", purchase.getPaymentMethod().getId()).firstResult();
            purchase.setPaymentMethod(paymentMethodMapper.toDto(paymentMethod));
        }
        log.info("##### {}", purchase);
        return purchase;
    }


    @Transactional
    public PurchaseDto saveDto(PurchaseDto dto, UserPortal buyer, Supplier supplier) {
        List<PurchaseItem> purchaseItems = purchaseItemMapper.toEntity(dto.getItens());
        PaymentMethod paymentMethod = PaymentMethod.find("id", dto.getPaymentMethod().getId()).firstResult();

        Purchase purchase = mapper.toEntity(dto);
        purchase.setBuyer(buyer);
        purchase.setSupplier(supplier);
        purchase.setSystemId(tokenService.getSystemId());
        purchase.setTotalValue(Purchase.calculateTotalValue(purchaseItems, BigDecimal.ZERO));
        purchase.setTotalQuantity(Purchase.calculateQuantity(purchaseItems, 0));
        purchase.setPaymentMethod(paymentMethod);
        purchase.persist();

        if(CollectionUtils.isNotEmpty(purchaseItems)) {
            for (PurchaseItem purchaseItem : purchaseItems) {
                if(purchaseItem.getQuantity() > 0) {
                    purchaseItem.setPurchase(purchase);
                    purchaseItem.persist();
                    packageLoanService.registryPackageLoan(purchaseItem);
                }
            }

            BigDecimal totalValue = purchase.getTotalValue();
            Integer payNumber = dto.getPayNumber();
            BigDecimal partialValue = purchase.getTotalValue()
                    .divide(BigDecimal.valueOf(payNumber), 2, RoundingMode.UP);

            for(int i = 0; i < payNumber; i++) {

                if(i == payNumber - 1) {
                    // Última parcela
                    partialValue = totalValue;
                } else {
                    totalValue = totalValue.subtract(partialValue);
                }

                AccountToPay accountToPay = new AccountToPay();
                accountToPay.setSystemId(tokenService.getSystemId());
                accountToPay.setSupplier(purchase.getSupplier());
                accountToPay.setOriginalValue(partialValue);
                accountToPay.setDueDate(purchase.getDueDate().plusMonths(i));
                accountToPay.setPurchase(purchase);
                accountToPay.setPaymentMethod(purchase.getPaymentMethod());
                if(paymentMethod.isAutoPayment()) {
                    accountToPay.setPaidOutDate(LocalDate.now());
                    accountToPay.setPaidOutValue(accountToPay.getOriginalValue());
                }
                accountToPay.persist();
            }

        }

        return mapper.toDto(purchase);
    }

    @Transactional
    public PurchaseDto saveDto(PurchaseDto dto) {

        UserPortal buyer = UserPortal.findById(dto.getBuyer().getId());
        Supplier supplier = Supplier.findById(dto.getSupplier().getId());

        return saveDto(dto, buyer, supplier);
    }

    @Transactional
    public PurchaseAppDto saveDtoFromApp(PurchaseAppDto appDto) {

        UserPortal buyer = appDto.getIdBuyer() != null ? UserPortal.findById(appDto.getIdBuyer()) : UserPortal.find("email", tokenService.getUserEmail()).firstResult();
        Supplier supplier = Supplier.findById(appDto.getIdSupplier());

        PurchaseDto purchaseDto = saveDto(purchaseAppMapper.from(appDto), buyer, supplier);

        return purchaseAppMapper.to(purchaseDto);
    }

    @Transactional
    public PurchaseAppDto update(Long id, PurchaseAppDto dto) {
        PurchaseDto result = update( id,  purchaseAppMapper.from(dto));
        return purchaseAppMapper.to(result);
    }

    @Transactional
    public List<PurchaseItemDto> update(List<PurchaseItemDto> purchaseItems) {
        if(CollectionUtils.isNotEmpty(purchaseItems)) {
            purchaseItems.forEach(purchaseItemDto -> {
                PurchaseItem purchaseItem = PurchaseItem.findById(purchaseItemDto.getId());
                purchaseItemMapper.updateEntityFromDto(purchaseItemDto, purchaseItem);
                purchaseItem.persist();
            });
        }
        return purchaseItems;
    }

    @Transactional
    public PurchaseDto update(Long id, PurchaseDto dto) {
        Purchase entity = Purchase.findById(id);

        mapper.updateEntityIgnoringNull(mapper.toEntity(dto), entity);
        List<PurchaseItem> purchaseItems = purchaseItemMapper.toEntity(dto.getItens());

        List<PackageLoan> packageLoans = PackageLoan.list("select p from PackageLoan p join p.purchaseItem pi " +
                "where pi.purchase.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id, tokenService.getSystemId());
        packageLoans.forEach(PackageLoan::delete);

        List<PurchaseDistribution> purchaseDistributions = PurchaseDistribution.list(
                "select p from PurchaseDistribution p join p.purchaseItem pi " +
                "where pi.purchase.id = ?1 and p.systemId = ?2 and p.deletedOn is null",
                id, tokenService.getSystemId());
        purchaseDistributions.forEach(PurchaseDistribution::delete);

        entity.setTotalQuantity(Purchase.calculateQuantity(purchaseItems, 0));
        entity.setTotalValue(Purchase.calculateTotalValue(purchaseItems, BigDecimal.ZERO));
        entity.persistAndFlush();

        PurchaseItem.delete("purchase.id=?1", id);

        for (PurchaseItem purchaseItem : purchaseItems) {
            if(purchaseItem.getQuantity() > 0) {
                purchaseItem.setId(null);
                purchaseItem.setPurchase(entity);
                PurchaseItem.persist(purchaseItem);
                packageLoanService.registryPackageLoan(purchaseItem);
            }
        }

        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Purchase.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}
