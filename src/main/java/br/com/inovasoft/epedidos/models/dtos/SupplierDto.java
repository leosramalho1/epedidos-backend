package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupplierDto {

    private Long id;
    private String name;
    private String cpfCnpj;
    private String contactName;
    private String phone;
    private StatusEnum status;
    private String email;
    private List<AddressDto> address = new ArrayList<>();

}