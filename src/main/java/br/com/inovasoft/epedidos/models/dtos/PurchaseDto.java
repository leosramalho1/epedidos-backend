package br.com.inovasoft.epedidos.models.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseDto {

    private Long id;
    private Long idBuyer;
    private String nameBuyer;
    private Long idSupplier;
    private String nameSupplier;
    private String dateRef;
    private List<PurchaseItemDto> itens;
}