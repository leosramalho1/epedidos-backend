package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDto {

    private Long id;
    private String name;
    private String cpfCnpj;
    private String contactName;
    private String phone;
    private String email;
    private StatusEnum status;
    private String payType;
    private BigDecimal payValue;
    private List<AddressDto> address = new ArrayList<>();
    private List<CustomerUserDto> users = new ArrayList<>();

}