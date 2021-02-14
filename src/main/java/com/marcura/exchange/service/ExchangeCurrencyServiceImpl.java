package com.marcura.exchange.service;

import com.marcura.exchange.models.dto.ExchangeRateDto;
import com.marcura.exchange.models.entity.CurrencyRates;
import com.marcura.exchange.repository.CurrencyRatesRepository;
import com.marcura.exchange.utils.SpreadEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ExchangeCurrencyServiceImpl implements ExchangeCurrencyService{

    private static final Logger logger = LoggerFactory.getLogger(ExchangeCurrencyServiceImpl.class);

    @Autowired
    private CurrencyRatesRepository currencyRatesRepository;

    @Override
    public ExchangeRateDto latestExchangeRate(String fromCurrency, String toCurrency) {

        logger.info("--Method--latestExchangeRate-- Calculating exchange rate from currency: {} to to currency: {}",fromCurrency,toCurrency);
        // Get exchange rates from database
        BigDecimal fromRate = getLatestRateByCurrency(fromCurrency);
        BigDecimal toRate = getLatestRateByCurrency(toCurrency);

        // Calculate max spread for currencies
        Integer maxSpread = getMaxSpreadValue(fromCurrency,toCurrency);
        logger.info("--Method--latestExchangeRate-- Calling calculateExchangeRate with fromRate: {}, toRate: {}, maxSpread: {}",fromRate,toRate,maxSpread);

        // Do the exchange rate calculation
        String exchangeRate = calculateExchangeRate(fromRate,toRate,maxSpread);
        logger.info("--Method--latestExchangeRate-- Calculated exchange rate: {}",exchangeRate);

        return ExchangeRateDto.builder()
                .from(fromCurrency)
                .to(toCurrency)
                .exchange(exchangeRate).build();
    }

    @Override
    public ExchangeRateDto exchangeRateAtDate(String fromCurrency, String toCurrency, LocalDate currencyDate) {

        logger.info("--Method--exchangeRateAtDate-- Calculating exchange rate from currency: {} to to currency: {} at date: {}",fromCurrency,toCurrency, currencyDate);
        // Get exchange rates from database
        BigDecimal fromRate = getLatestRateByCurrencyAndDate(fromCurrency,currencyDate);
        BigDecimal toRate = getLatestRateByCurrencyAndDate(toCurrency,currencyDate);

        // Calculate max spread for currencies
        Integer maxSpread = getMaxSpreadValue(fromCurrency,toCurrency);
        logger.info("--Method--exchangeRateAtDate-- Calling calculateExchangeRate with fromRate: {}, toRate: {}, maxSpread: {}",fromRate,toRate,maxSpread);

        // Do the exchange rate calculation
        String exchangeRate = calculateExchangeRate(fromRate,toRate,maxSpread);
        logger.info("--Method--exchangeRateAtDate-- Calculated exchange rate: {} ",exchangeRate);

        return ExchangeRateDto.builder()
                .from(fromCurrency)
                .to(toCurrency)
                .exchange(exchangeRate).build();
    }

    protected BigDecimal getLatestRateByCurrency(String currency){
        // Fetch result from db by date and currency
        CurrencyRates currencyRates = currencyRatesRepository.findFirstByCurrencyEqualsOrderByRateDateDesc(currency);
        // If result not found throw NoSuchElementException to catch in controller layer and throw 404 not found exception
        Optional.ofNullable(currencyRates).orElseThrow(NoSuchElementException::new);
        // Increment operation count for each usage
        currencyRates.setOperationCount(currencyRates.getOperationCount()+1);
        currencyRatesRepository.saveAndFlush(currencyRates);

        return currencyRates.getRate();
    }

    protected BigDecimal getLatestRateByCurrencyAndDate(String currency, LocalDate date){
        // Fetch result from db by date and currency
        CurrencyRates currencyRates = currencyRatesRepository.findFirstByCurrencyEqualsAndRateDateEquals(currency, date);
        // If result not found throw NoSuchElementException to catch in controller layer and throw 404 not found exception
        Optional.ofNullable(currencyRates).orElseThrow(NoSuchElementException::new);
        // Increment operation count for each usage
        currencyRates.setOperationCount(currencyRates.getOperationCount()+1);
        currencyRatesRepository.saveAndFlush(currencyRates);

        return currencyRates.getRate();
    }

    protected String calculateExchangeRate(BigDecimal fromRate, BigDecimal toRate, Integer maxSpread) {
        logger.info("--Method--calculateExchangeRate-- Entering with parameters fromRate: {}, toRate: {}, maxSpread: {} ",fromRate,toRate,maxSpread);
        // if both exchange rates are same return 0
        if(fromRate.compareTo(toRate) == 0){
            return "0.00";
        }
        // Part1: toCurrencyExchangeRateToUSD / fromCurrencyExchangeToUSD
        BigDecimal firstDivisionOp = toRate.divide(fromRate,3,RoundingMode.HALF_UP);
        logger.info("--Method--calculateExchangeRate-- firstDivisionOp: {}",firstDivisionOp);
        // Part2:  (100 - MAX(toCurrencySpread,fromCurrencySpread)) / 100
        BigDecimal secondDivisionOp = BigDecimal.valueOf((long)100-maxSpread).divide(BigDecimal.valueOf(100),3,RoundingMode.HALF_UP);
        logger.info("--Method--calculateExchangeRate-- secondDivisionOp: {}",secondDivisionOp);
        // Part3 final calculation returning 2 decimal point result
        BigDecimal result = firstDivisionOp.multiply(secondDivisionOp).setScale(2, RoundingMode.HALF_UP);
        logger.info("--Method--calculateExchangeRate-- result: {}",result);
        return result.toString();
    }

    protected Integer getMaxSpreadValue(String fromCurrency, String toCurrency)
    {
       return  Math.max(SpreadEnum.getSpreadByCurrency(toCurrency), SpreadEnum.getSpreadByCurrency(fromCurrency));
    }
}
