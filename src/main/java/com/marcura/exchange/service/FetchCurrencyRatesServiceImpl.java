package com.marcura.exchange.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcura.exchange.models.dto.ExchangeRateApiDto;
import com.marcura.exchange.models.entity.CurrencyRates;
import com.marcura.exchange.repository.CurrencyRatesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FetchCurrencyRatesServiceImpl implements FetchCurrencyRatesService {

    // Creates and httpClient for retrieving data from Exchange Rate API
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final String API_URL ="https://api.exchangeratesapi.io/latest?base=USD";
    private static final String BASE_CURRENCY = "USD";
    private static final Integer OPERATION_COUNT = 0;

    private static final Logger logger = LoggerFactory.getLogger(FetchCurrencyRatesServiceImpl.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' hh:mm a");

    @Autowired
    private CurrencyRatesRepository currencyRatesRepository;

    @Override
    public void retrieveRates() throws Exception {
        String currentDateTime = dateTimeFormatter.format(LocalDateTime.now());
        logger.info("RetrieveRates :: Execution Time - {}", currentDateTime);

        // RetrieveRates From Api using Java 11 HttpClient
        ExchangeRateApiDto exchangeRateApiDto = getRatesFromApi();
        // Update/Insert rates to db
        saveExchangeRates(exchangeRateApiDto);

    }

    private void saveExchangeRates(ExchangeRateApiDto exchangeRateApiDto) {
        // Use the date field from the result of exchange rate api to check existing records
        LocalDate rateDate = LocalDate.parse(exchangeRateApiDto.getDate());
        List<CurrencyRates> currencyRatesList = currencyRatesRepository.findAllByRateDate(rateDate);

        // If there is no existing rate for the date insert new records otherwise update the existing records.
        if(currencyRatesList.isEmpty())
        {
            List<CurrencyRates> rateEntityList = exchangeRateApiDto.getRates()
                    .entrySet()
                    .stream()
                    .map(m-> {
                                CurrencyRates rateEntity = new CurrencyRates();
                                rateEntity.setCurrency(m.getKey());
                                rateEntity.setRate(new BigDecimal(m.getValue()));
                                rateEntity.setBaseCurrency(BASE_CURRENCY);
                                rateEntity.setOperationCount(OPERATION_COUNT);
                                rateEntity.setRateDate(rateDate);
                                return rateEntity;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            currencyRatesRepository.saveAll(rateEntityList);
            logger.info("saveExchangeRates :: Inserted new records");
        }else
        {
            // Update only the rate field do not change counter
            currencyRatesList.forEach( currencyRates -> {
                BigDecimal rate = new BigDecimal(exchangeRateApiDto.getRates().get(currencyRates.getCurrency()));
                currencyRates.setRate(rate);
            });

            currencyRatesRepository.saveAll(currencyRatesList);
            logger.info("saveExchangeRates :: Updated existing records");
        }
    }

    private ExchangeRateApiDto getRatesFromApi() throws IOException, InterruptedException {
        // Get latest currency rates based on USD
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(API_URL))
                .setHeader("User-Agent", "Exchange Demo App")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        // Map the results to dto by using jackson mapper.
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(),ExchangeRateApiDto.class);
    }

}
