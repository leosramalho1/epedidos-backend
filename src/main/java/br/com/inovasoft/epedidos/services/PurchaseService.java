package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.mappers.PurchaseItemMapper;
import br.com.inovasoft.epedidos.mappers.PurchaseMapper;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.dtos.PurchaseDto;
import br.com.inovasoft.epedidos.models.dtos.PurchaseGroupDto;
import br.com.inovasoft.epedidos.models.dtos.PurchaseItemDto;
import br.com.inovasoft.epedidos.models.dtos.UserPortalDto;
import br.com.inovasoft.epedidos.models.entities.*;
import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import br.com.inovasoft.epedidos.models.enums.PackageTypeEnum;
import br.com.inovasoft.epedidos.security.TokenService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@ApplicationScoped
public class PurchaseService extends BaseService<Purchase> {

    @Inject
    TokenService tokenService;

    @Inject
    PurchaseMapper mapper;

    @Inject
    PurchaseItemMapper purchaseItemmapper;

    public PaginationDataResponse<PurchaseDto> listAll(int page) {
        String query = "systemId = ?1 and deletedOn is null";
        PanacheQuery<Purchase> listPurchases = Purchase.find(query, Sort.by("id").descending(),
                tokenService.getSystemId());

        List<Purchase> dataList = listPurchases.page(Page.of(page - 1, limitPerPage)).list();

        return new PaginationDataResponse<>(mapper.toDto(dataList), limitPerPage,
                (int) Purchase.count(query, tokenService.getSystemId()));
    }


    public List<PurchaseDto> listPurchasesByBuyer() {
        PanacheQuery<Purchase> listPurchases = Purchase.find(" select new Purchase(id, supplier.name, createdOn, totalValue ) from Purchase where buyer.email = ?1 and deletedOn is null",
                tokenService.getUserEmail());

        return mapper.toDto(listPurchases.list());
    }

    public Purchase findById(Long id) {
        return Purchase.find("select p from Purchase p where p.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id,
                tokenService.getSystemId()).firstResult();
    }

    public PurchaseGroupDto getOpenOrderAndGroupByIdBuyer(Long buyerId) {
        LocalDate yesterday = LocalDate.now();//.minusDays(-1);

        List<OrderItem> ordersItems = OrderItem.list(
                "select oi from OrderItem oi where oi.order.status=?1 " +
                        "and oi.product.buyerId= ?2 and oi.order.createdOn <= ?3 " +
                        "and oi.order.deletedOn is null order by oi.product.id",
                OrderEnum.OPEN, buyerId, yesterday.atTime(23, 59, 59));

        List<PurchaseItem> purchaseItems = PurchaseItem.list(
                "select pi from PurchaseItem pi where pi.purchase.deletedOn is null and pi.purchase.status=?1 " +
                        "and pi.purchase.buyer.id=?2 and pi.purchase.createdOn <= ?3 " +
                        "order by pi.product.id",
                OrderEnum.OPEN, buyerId, yesterday.atTime(23, 59, 59));

        return mountPurchaseGroup(buyerId, yesterday, ordersItems, purchaseItems);
    }

    public PurchaseDto getProductsToBuy(Long buyerId) {
        List<Product> products = Product.list("buyerId=?1 order by name", buyerId);

        List<PurchaseItemDto> items = products.stream()
                .map(product -> new PurchaseItemDto(product.getId(), product.getName(), 0))
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

        if (CollectionUtils.isNotEmpty(ordersItems )) {
            for (OrderItem item : ordersItems) {
                PurchaseItemDto purchaseItemDto = map.get(item.getProduct().getId());
                if (purchaseItemDto == null) {
                    purchaseItemDto = new PurchaseItemDto();
                    purchaseItemDto.setIdProduct(item.getProduct().getId());
                    purchaseItemDto.setNameProduct(item.getProduct().getName());
                    purchaseItemDto.setQuantity(item.getQuantity());
                    purchaseItemDto.setUnitValue(new BigDecimal("0.00"));
                    purchaseItemDto.setTotalValue(new BigDecimal("0.00"));

                    map.put(item.getProduct().getId(), purchaseItemDto);
                } else {
                    purchaseItemDto.setQuantity(item.getQuantity() + purchaseItemDto.getQuantity());
                }
            }
        }

        if (CollectionUtils.isNotEmpty(purchaseItems) && MapUtils.isNotEmpty(map)) {
            for (PurchaseItem item : purchaseItems) {
                PurchaseItemDto purchaseItemDto = map.get(item.getProduct().getId());
                purchaseItemDto.setQuantity(purchaseItemDto.getQuantity() - item.getQuantity());
            }

        }

        List<PurchaseItemDto> items = new ArrayList<>(map.values());
        purchaseGroup.setItens(items.stream().filter(item -> item.getQuantity() > 0).collect(Collectors.toList()));

        items.sort(Comparator.comparing(PurchaseItemDto::getNameProduct, Comparator.naturalOrder()))    ;

        return purchaseGroup;
    }

    public PurchaseDto findDtoById(Long id) {
        Purchase entity = findById(id);

        PurchaseDto purchase = mapper.toDto(entity);

        purchase.setItens(purchaseItemmapper
                .toDto(PurchaseItem.list("purchase.id = ?1 Purchase by product.name", purchase.getId())));

        return purchase;
    }

    @Transactional
    public PurchaseDto saveDto(PurchaseDto dto) {
        Purchase purchase = mapper.toEntity(dto);
        purchase.setCreatedOn(LocalDateTime.now());

        UserPortal userPortal = UserPortal.find("email", tokenService.getUserEmail()).firstResult();

        purchase.setBuyer(dto.getIdBuyer() !=null?UserPortal.findById(dto.getIdBuyer()):userPortal!=null?userPortal:UserPortal.findById(dto.getBuyer() != null && dto.getBuyer().getId() !=null?dto.getBuyer().getId():dto.getIdBuyer()));
        purchase.setSupplier(Supplier.findById(dto.getSupplier() != null && dto.getSupplier().getId() !=null?dto.getSupplier().getId(): dto.getIdSupplier()));
        purchase.setSystemId(tokenService.getSystemId());
        BigDecimal totalValueProducts = dto.getItens().stream().map(PurchaseItemDto::getTotalValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        purchase.setTotalValue(totalValueProducts);
        purchase.persist();

        List<PurchaseItem> purchaseItems = purchaseItemmapper.toEntity(dto.getItens());
        if(CollectionUtils.isNotEmpty(purchaseItems)) {
            for (PurchaseItem purchaseItem : purchaseItems) {
                purchaseItem.setPurchase(purchase);
                purchaseItem.persist();

                Product product = Product.findById(purchaseItem.getProduct().getId());

                savePackageType(purchase, purchaseItem, product);

            }

            AccountToPay accountToPay = new AccountToPay();
            accountToPay.setSystemId(tokenService.getSystemId());
            accountToPay.setSupplier(purchase.getSupplier());
            accountToPay.setOriginalValue(totalValueProducts);
            accountToPay.setDueDate(purchase.getDueDate());
            accountToPay.persist();

        }

        return mapper.toDto(purchase);
    }

    private void savePackageType(Purchase purchase, PurchaseItem purchaseItem, Product product) {
        if (product.getPackageType() == PackageTypeEnum.RETURNABLE) {
            PackageLoan packageLoan = new PackageLoan();
            packageLoan.setSystemId(tokenService.getSystemId());
            packageLoan.setSupplier(purchase.getSupplier());
            packageLoan.setPurchaseItem(purchaseItem);
            packageLoan.setUserChange(tokenService.getUserEmail());
            packageLoan.setBorrowedAmount(purchaseItem.getQuantity().longValue());
            packageLoan.persist();
        }
    }

    @Transactional
    public PurchaseDto update(Long id, PurchaseDto dto) {
        Purchase entity = Purchase.findById(id);

        mapper.updateEntityFromDto(dto, entity);

        List<PackageLoan> packageLoans = PackageLoan.list("select p from PackageLoan p join p.purchaseItem pi " +
                "where pi.purchase.id = ?1 and p.systemId = ?2 and p.deletedOn is null", id, tokenService.getSystemId());
        packageLoans.forEach(PackageLoan::delete);
        entity.persistAndFlush();
        PurchaseItem.delete("purchase.id=?1", id);

        List<PurchaseItem> itens = purchaseItemmapper.toEntity(dto.getItens());
        for (PurchaseItem item : itens) {
            item.setId(null);
            item.setPurchase(entity);
            PurchaseItem.persist(item);
            savePackageType(entity, item, Product.findById(item.getProduct().getId()));
        }

        return mapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        Purchase.update("set deletedOn = now() where id = ?1 and systemId = ?2", id, tokenService.getSystemId());
    }

}
