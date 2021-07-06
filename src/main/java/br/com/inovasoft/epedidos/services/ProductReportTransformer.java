package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.models.dtos.ProductReportDto;
import org.hibernate.transform.ResultTransformer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class ProductReportTransformer implements ResultTransformer {

    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        int i = -1;
        return new ProductReportDto(((BigInteger) objects[++i]).longValueExact(),
                (String) objects[++i], (BigDecimal) objects[++i],
                ((BigInteger) objects[++i]).longValueExact(),
                (BigDecimal) objects[++i], (BigDecimal) objects[++i], ((BigInteger) objects[++i]).longValueExact());
    }

    @Override
    public List<ProductReportDto> transformList(List list) {
        return list;
    }
}
