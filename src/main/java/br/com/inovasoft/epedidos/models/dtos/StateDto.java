package br.com.inovasoft.epedidos.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateDto {
    private Long id;
    private String name;
    private Long code;
    private String initials;
    private BigDecimal latitude;
    private BigDecimal longitude;
}