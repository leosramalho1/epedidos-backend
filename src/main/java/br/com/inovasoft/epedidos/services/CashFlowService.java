package br.com.inovasoft.epedidos.services;

import br.com.inovasoft.epedidos.models.dtos.AccountToPayDto;
import br.com.inovasoft.epedidos.models.dtos.AccountToReceiveDto;
import br.com.inovasoft.epedidos.models.dtos.CashFlowDto;
import br.com.inovasoft.epedidos.models.dtos.PaginationDataResponse;
import br.com.inovasoft.epedidos.models.enums.PayStatusEnum;

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

    public PaginationDataResponse<CashFlowDto> listAll(int page, LocalDate dateMin, LocalDate dateMax) {

        List<PayStatusEnum> status = List.of(PayStatusEnum.PAID);

        PaginationDataResponse<AccountToPayDto> accountToPayDto = accountToPayService.listAll(page,
                status, null, null, null, null, dateMin, dateMax);
        PaginationDataResponse<AccountToReceiveDto> accountToReceiveDto = accountToReceiveService.listAll(page,
                status,null, null, null, null, dateMin, dateMax);

        List<CashFlowDto> list = buildCashFlowByDate(accountToReceiveDto.getData(), accountToPayDto.getData());

        List<CashFlowDto> pageList = list.stream()
                .skip((page - 1) * BaseService.limitPerPage)
                .limit(BaseService.limitPerPage)
                .collect(Collectors.toList());

        return new PaginationDataResponse<>(pageList, BaseService.limitPerPage, list.size());
    }


    private List<CashFlowDto> buildCashFlowByDate(List<AccountToReceiveDto> accountToReceive, List<AccountToPayDto> accountToPay) {

        Map<LocalDate, List<AccountToReceiveDto>> accountToReceiveDtoMap = accountToReceive.stream()
                .collect(Collectors.groupingBy(AccountToReceiveDto::getPaidOutDate));

        Map<LocalDate, List<AccountToPayDto>> accountToPayDtoMap = accountToPay.stream()
                .collect(Collectors.groupingBy(AccountToPayDto::getPaidOutDate));

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