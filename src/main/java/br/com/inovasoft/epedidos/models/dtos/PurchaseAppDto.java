package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.PurchaseEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseAppDto {

    private Long id;
    private Long idSupplier;
    private String supplierName;
    private Long idBuyer;
    private String buyerName;
    private String dateRef;
    private PurchaseEnum status;
    private List<PurchaseItemDto> itens;
    private String dueDate;
    private String createdOn;
    private Integer payNumber;
    private String payMethod;

  
}