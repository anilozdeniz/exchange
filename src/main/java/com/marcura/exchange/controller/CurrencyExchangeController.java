package com.marcura.exchange.controller;

import com.marcura.exchange.models.dto.ExchangeRateDto;
import com.marcura.exchange.service.ExchangeCurrencyService;
import com.marcura.exchange.service.FetchCurrencyRatesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("")
@Api(value="CurrencyExchange", description="This is a sample app for caching exchange rates to a local database and calculating spread percentage from db")
public class CurrencyExchangeController {

    @Autowired
    private ExchangeCurrencyService exchangeCurrencyService;

    @Autowired
    private FetchCurrencyRatesService fetchCurrencyRatesService;

    @ApiOperation(value = "Gets the latest exchange rate from local db",response = ExchangeRateDto.class)
    @RequestMapping(value = "/exchange", params = {"from","to"} ,method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<ExchangeRateDto> getLatestExchangeRate(
            @RequestParam("from") String fromCurrency, @RequestParam("to") String toCurrency){
            try{
                return new ResponseEntity<>(exchangeCurrencyService.latestExchangeRate(fromCurrency,toCurrency), HttpStatus.OK);
            }catch (NoSuchElementException exception)
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Currency not found");
            }
    }

    @ApiOperation(value = "Gets the exchange date at provided date from local db",response = ExchangeRateDto.class)
    @RequestMapping(value = "/exchange-date", params = {"from","to", "date"} , method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<ExchangeRateDto> exchangeRateAtDate(
            @RequestParam("from") String fromCurrency, @RequestParam("to") String toCurrency,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currencyDate){
            try{
                return new ResponseEntity<>(exchangeCurrencyService.exchangeRateAtDate(fromCurrency,toCurrency,currencyDate), HttpStatus.OK);
            }catch (NoSuchElementException exception)
            {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Currency not found");
            }
    }

    @ApiOperation(value = "Insert/Update the latest exchange rate to local db",response = HttpStatus.class)
    @RequestMapping(value = "/exchange", method= RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<HttpStatus> fetchLatestExchangeRate(){

        try {
            fetchCurrencyRatesService.retrieveRates();
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error during insert/update operation");
        }
    }
}


