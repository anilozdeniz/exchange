package com.marcura.exchange.service;

import com.marcura.exchange.models.dto.ExchangeRateDto;

import java.time.LocalDate;

public interface ExchangeCurrencyService {

    ExchangeRateDto latestExchangeRate(String fromCurrency, String toCurrency);

    ExchangeRateDto exchangeRateAtDate(String fromCurrency, String toCurrency, LocalDate currencyDate);

}
