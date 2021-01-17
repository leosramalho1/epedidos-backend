package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.OrderEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseDto {

    private Long id;
    private Long idBuyer;
    private String nameBuyer;
    private SupplierDto supplier;
    private String dateRef;
    private OrderEnum status;
    private List<PurchaseItemDto> itens;
    private LocalDate dueDate;
}