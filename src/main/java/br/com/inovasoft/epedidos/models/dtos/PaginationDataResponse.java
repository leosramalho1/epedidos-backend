package br.com.inovasoft.epedidos.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginationDataResponse<T> {

    private List<T> data;
    private int limit;
    private int count;

    public PaginationDataResponse() {
        super();
    }
}