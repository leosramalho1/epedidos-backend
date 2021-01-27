package br.com.inovasoft.epedidos.models.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseAppDto {

    private Long id;
    private Long idSupplier;
    private String supplierName;
    private Long idBuyer;
    private String buyerName;
    private String dateRef;
    private OrderEnum status;
    private List<PurchaseItemDto> itens;
    private String dueDate;
    private String createdOn;
    private Integer payNumber;
    private String payMethod;

  
}