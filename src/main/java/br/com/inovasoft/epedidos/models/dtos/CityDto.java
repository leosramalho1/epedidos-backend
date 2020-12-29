package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CityDto {
    private Long id;
    private Long code;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private boolean capital;
    private StateDto state;
}