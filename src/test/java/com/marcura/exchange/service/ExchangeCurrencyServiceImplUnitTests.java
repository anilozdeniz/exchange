package com.marcura.exchange.service;

import com.marcura.exchange.models.dto.ExchangeRateDto;
import com.marcura.exchange.models.entity.CurrencyRates;
import com.marcura.exchange.repository.CurrencyRatesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;



@RunWith(SpringRunner.class)
public class ExchangeCurrencyServiceImplUnitTests {

    public static final String USD = "USD";
    public static final String EUR = "EUR";
    public static final String PLN = "PLN";
    public static final String RATE_DATE = "2021-02-12";
    public static final int OPERATION_COUNT = 0;

    @TestConfiguration
    static class ExchangeCurrencyServiceImplTestsContextConfiguration {

        @Bean
        public ExchangeCurrencyService exchangeCurrencyService() { return new ExchangeCurrencyServiceImpl();}

    }

    @Autowired
    private ExchangeCurrencyService exchangeCurrencyService;

    @MockBean
    private CurrencyRatesRepository currencyRatesRepository;

    private CurrencyRates currencyRatesEur;
    private CurrencyRates currencyRatesPln;

    @Before
    public void setUp(){

        currencyRatesEur = new CurrencyRates().builder()
                .currency(EUR)
                .rate(new BigDecimal("0.8"))
                .rateDate(LocalDate.parse(RATE_DATE))
                .baseCurrency(USD)
                .operationCount(OPERATION_COUNT)
                .build();

        currencyRatesPln = new CurrencyRates().builder()
                .currency(PLN)
                .rate(new BigDecimal("3.7"))
                .rateDate(LocalDate.parse(RATE_DATE))
                .baseCurrency(USD)
                .operationCount(OPERATION_COUNT)
                .build();

    }

    @Test
    public void whenBothCurrenciesSameShouldReturnZeroRate()
    {
        //Given
        BigDecimal fromRate = BigDecimal.valueOf(2.00);
        BigDecimal toRate = BigDecimal.valueOf(2.00);
        String expected = "0.00";
        Integer maxSpread = 4;
        //When
        ExchangeCurrencyServiceImpl exchangeCurrencyServ = new ExchangeCurrencyServiceImpl();
        String actual = exchangeCurrencyServ.calculateExchangeRate(fromRate,toRate,maxSpread);
        //Then
        assertEquals(expected,actual);
    }
    @Test
    public void shouldCalculateExchangeRate()
    {
        //Given
        BigDecimal toRate = BigDecimal.valueOf(3.7);
        BigDecimal fromRate = BigDecimal.valueOf(0.8);
        Integer maxSpread = 4;
        String expected = "4.44";
        //When
        ExchangeCurrencyServiceImpl exchangeCurrencyServ = new ExchangeCurrencyServiceImpl();
        String actual = exchangeCurrencyServ.calculateExchangeRate(fromRate,toRate,maxSpread);
        //Then
        assertEquals(expected,actual);
    }

    @Test
    public void shouldReturnMaxSpreadValue()
    {
        // Given
        String fromCurrency = "JPY";  /*3*/
        String toCurrency = "ZAR";  /*6*/
        Integer expected = 6;
        //When
        ExchangeCurrencyServiceImpl exchangeCurrencyServ = new ExchangeCurrencyServiceImpl();
        Integer actual = exchangeCurrencyServ.getMaxSpreadValue(fromCurrency,toCurrency);
        // Then
        assertEquals(expected,actual);
    }

    @Test
    public void whenLatestExchangeRateWithValidArgumentsThenResultCalculatedCorrectly()
    {
        //Given
        String fromCurrency = EUR;
        String toCurrency = PLN;
        ExchangeRateDto expected = ExchangeRateDto.builder()
                .from(EUR)
                .to(PLN)
                .exchange("4.53")
                .build();
        //When
        Mockito.when(currencyRatesRepository.findFirstByCurrencyEqualsOrderByRateDateDesc(EUR)).thenReturn(currencyRatesEur);
        Mockito.when(currencyRatesRepository.findFirstByCurrencyEqualsOrderByRateDateDesc(PLN)).thenReturn(currencyRatesPln);
        ExchangeRateDto actual = exchangeCurrencyService.latestExchangeRate(fromCurrency,toCurrency);
        // Then
        assertEquals(expected.getFrom(),actual.getFrom());
        assertEquals(expected.getTo(),actual.getTo());
        assertEquals(expected.getExchange(),actual.getExchange());
    }

    @Test(expected = NoSuchElementException.class)
    public void whenLatestExchangeRateNotFoundThenException()
    {
        //Given
        String fromCurrency = EUR;
        String toCurrency = PLN;
        //When
        Mockito.when(currencyRatesRepository.findFirstByCurrencyEqualsOrderByRateDateDesc(any())).thenReturn(null);
        exchangeCurrencyService.latestExchangeRate(fromCurrency,toCurrency);
        // Then exception will be thrown

    }

    @Test
    public void whenExchangeRateAtDateValidArgumentsThenResultCalculatedCorrectly()
    {
        //Given
        String fromCurrency = EUR;
        String toCurrency = PLN;
        ExchangeRateDto expected = ExchangeRateDto.builder()
                .from(EUR)
                .to(PLN)
                .exchange("4.53")
                .build();
        //When
        Mockito.when(currencyRatesRepository.findFirstByCurrencyEqualsAndRateDateEquals(EUR,LocalDate.parse(RATE_DATE))).thenReturn(currencyRatesEur);
        Mockito.when(currencyRatesRepository.findFirstByCurrencyEqualsAndRateDateEquals(PLN,LocalDate.parse(RATE_DATE))).thenReturn(currencyRatesPln);
        ExchangeRateDto actual = exchangeCurrencyService.exchangeRateAtDate(fromCurrency,toCurrency,LocalDate.parse(RATE_DATE));

        // Then
        assertEquals(expected.getFrom(),actual.getFrom());
        assertEquals(expected.getTo(),actual.getTo());
        assertEquals(expected.getExchange(),actual.getExchange());
    }

    @Test(expected = NoSuchElementException.class)
    public void whenExchangeRateAtDateNotFoundThenException()
    {
        //Given
        String fromCurrency = EUR;
        String toCurrency = PLN;
        //When
        Mockito.when(currencyRatesRepository.findFirstByCurrencyEqualsAndRateDateEquals(any(),any())).thenReturn(null);
        exchangeCurrencyService.exchangeRateAtDate(fromCurrency,toCurrency,LocalDate.parse(RATE_DATE));
        // Then exception will be thrown
    }

}


