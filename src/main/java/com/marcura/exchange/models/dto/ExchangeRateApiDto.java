package com.marcura.exchange.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateApiDto {

    private Map<String,String> rates;
    private String base;
    private String date;

}
