package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.models.dtos.GroupProductsReportDto;
import br.com.inovasoft.epedidos.models.dtos.OrderClosedReportDto;
import br.com.inovasoft.epedidos.models.dtos.ProductReportDto;
import br.com.inovasoft.epedidos.security.TokenService;
import br.com.inovasoft.epedidos.util.FormatUtil;
import br.com.inovasoft.epedidos.util.RelatorioUtil;
import com.google.common.collect.Lists;
import com.itextpdf.text.DocumentException;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class ReportService {

    @Inject
    RelatorioUtil relatorioUtil;

    @Inject
    CustomerService customerService;

    @Inject
    TokenService tokenService;

    @Inject
    EntityManager em;

    private String trataFiltroSeExistirData(LocalDate initDate) {
        return Objects.nonNull(initDate) ? " and date(?) in (select date(cr.createdon) from conta_receber cr where cr.id = cd.conta_receber_id) " : StringUtils.EMPTY;
    }

    private String trataJoinSeExisteData(LocalDate initDate) {
        return Objects.nonNull(initDate) ? "left join conta_receber cr on cr.id = cd.conta_receber_id " : StringUtils.EMPTY;
    }

    public byte[] products(LocalDate initDate) throws DocumentException, IOException {
        List<byte[]> pdfs = customerService.listActive().stream()
                .map(c -> {
                    try {
                        return products(c.getId(), initDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return relatorioUtil.mergePdfFiles(pdfs);

    }

    public byte[] products(Long customerId, LocalDate initDate) throws DocumentException, CssResolverException, IOException {

        if(Objects.isNull(customerId)) {
            return products(initDate);
        }

        Query query = em.createNativeQuery(
                "SELECT " +
                "pr.id, " +
                "pr.nome, " +
                "sum(coalesce(ci.peso, 0) * coalesce(cd.quantidade_distribuida, 1)) / sum(coalesce(cd.quantidade_distribuida, 1)) as peso, " +
                "sum(coalesce(cd.quantidade_distribuida, 0)) as quantidade, " +
                "sum(coalesce(cd.quantidade_distribuida, 1) * " +
                "(coalesce(cd.valor_frete, 0) + coalesce(cd.valor_cliente_unitario, 0) + coalesce(coalesce(cd.valor_unitario, ci.valor_unitario), 0))) / sum(coalesce(cd.quantidade_distribuida, 1))  as valor_unitario, " +
                "coalesce(pr.margem_lucro, 1) as margem_lucro, " +
                "coalesce(ei.quantidade_emprestada, 0) as quantidade_emprestada " +
                "from produto pr " +
                "left join compra_distribuicao cd on cd.produto_id = pr.id and cd.conta_receber_id is not null and cd.cliente_id = ? and pr.sistema_id = ? " + trataFiltroSeExistirData(initDate) +
                "left join pedido_item pi on pi.id = cd.pedido_item_id " +
                "left join compra_item ci on ci.id = cd.compra_item_id " +
                "left join emprestimo_embalagem ei on ei.order_item_id = cd.pedido_item_id and quantidade_emprestada > quantidade_devolvida " +
                "where pr.status = 'ACTIVE' " +
                "group by pr.id, ei.quantidade_emprestada " +
                "order by pr.nome ")
                .setParameter(1, customerId)
                .setParameter(2, tokenService.getSystemId());

        if(Objects.nonNull(initDate)) {
            query.setParameter(3, initDate.toString());
        }

        List<ProductReportDto> products = (List<ProductReportDto>) query
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new ProductReportTransformer())
                .getResultList();

        List<List<ProductReportDto>> partition = Lists.partition(products, products.size()/2);
        List<ProductReportDto> productReportDtos1 = partition.get(0);
        List<ProductReportDto> productReportDtos2 = partition.get(1);

        int minSize = Integer.min(productReportDtos1.size(), productReportDtos2.size());
        List<GroupProductsReportDto> report = new ArrayList<>();

        int i;

        for (i = 0; i < minSize; i++) {
            report.add(GroupProductsReportDto.builder()
                    .first(productReportDtos1.get(i))
                    .second(productReportDtos2.get(i))
                    .build());
        }

        for (; i < productReportDtos1.size(); i++) {
            report.add(GroupProductsReportDto.builder()
                    .first(productReportDtos1.get(i))
                    .second(new ProductReportDto())
                    .build());
        }

        OrderClosedReportDto orderClosedReportDto = OrderClosedReportDto.builder()
                .customer(customerService.findDtoById(customerId))
                .products(report)
                .totalValue(FormatUtil.formataValor(OrderClosedReportDto.calculateTotalValue(report)))
                .totalPackageLoan(FormatUtil.formataNumero(OrderClosedReportDto.calculateTotalPackageLoan(report)))
                .dateReport(Objects.nonNull(initDate) ? "Data: " + FormatUtil.formataData(initDate, FormatUtil.FORMAT_DD_MM_YYYY) : "")
                .build();

        return relatorioUtil.gerarRelatorioPedidosFechados(orderClosedReportDto);
    }

}
