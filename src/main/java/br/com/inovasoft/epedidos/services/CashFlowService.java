package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.models.dtos.AccountToPayDto;
import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.dtos.CashFlowDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class CashFlowService {

    @Inject
    AccountToPayService accountToPayService;

    @Inject
    AccountToReceiveService accountToReceiveService;

    public PaginationDataResponse<CashFlowDto> listAll(int page) {

        PaginationDataResponse<AccountToPayDto> accountToPayDto = accountToPayService.listInactive(page);
        PaginationDataResponse<AccountToReceiveDto> accountToReceiveDto = accountToReceiveService.listInactive(page);

        List<CashFlowDto> list = buildCashFlowByDate(accountToReceiveDto.getData(), accountToPayDto.getData());
        return new PaginationDataResponse<>(list, list.size(), list.size());
    }


    private List<CashFlowDto> buildCashFlowByDate(List<AccountToReceiveDto> accountToReceive, List<AccountToPayDto> accountToPay) {

        Map<LocalDate, List<AccountToReceiveDto>> accountToReceiveDtoMap = accountToReceive.stream()
                .collect(Collectors.groupingBy(AccountToReceiveDto::getReceiveDate));

        Map<LocalDate, List<AccountToPayDto>> accountToPayDtoMap = accountToPay.stream()
                .collect(Collectors.groupingBy(AccountToPayDto::getPayDate));

        Set<LocalDate> datesToReceive = accountToReceiveDtoMap.keySet();
        Set<LocalDate> datesToPay = accountToPayDtoMap.keySet();

        Set<LocalDate> dates = new HashSet<>();
        dates.addAll(datesToReceive);
        dates.addAll(datesToPay);

        return dates.stream()
                .map(d -> CashFlowDto.builder().cashFlowDate(d)
                        .accountsToReceive(accountToReceiveDtoMap.get(d))
                        .accountsToPay(accountToPayDtoMap.get(d)).build())
                .sorted(Comparator.comparing(CashFlowDto::getCashFlowDate))
                .collect(Collectors.toList());
    }

}