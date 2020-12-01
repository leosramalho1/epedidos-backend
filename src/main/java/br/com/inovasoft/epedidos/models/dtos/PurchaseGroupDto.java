package br.com.inovasoft.epedidos.models.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PurchaseGroupDto {

    private Long idBuyer;
    private String nameBuyer;
    private String dateRef;
    private List<PurchaseItemDto> itens;
}