package br.com.inovasoft.epedidos.models.dtos;

import br.com.inovasoft.epedidos.models.enums.CustomerPayTypeEnum;
import br.com.inovasoft.epedidos.models.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class CustomerDto {

    private Long id;
    private String name;
    private String cpfCnpj;
    private String contactName;
    private String phone;
    private String email;
    private StatusEnum status;
    private CustomerPayTypeEnum payType;
    private BigDecimal payValue;
    private List<AddressDto> address = new ArrayList<>();
    private List<CustomerUserDto> users = new ArrayList<>();


    private List<AccountToReceiveDto> accountToReceives = new ArrayList<>();

}