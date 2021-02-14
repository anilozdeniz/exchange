package com.marcura.exchange;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ExchangeApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application.properties")
public class ExchangeCurrencyServiceIntegrationTest {

    @Autowired
    private MockMvc mvc;


    @Test
    public void givenCurrencies_whenExchange_thenStatus200()
            throws Exception {

        mvc.perform(get("/exchange?from=CAD&to=HKD")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenCurrenciesForExistingDate_when_ExchangeDate_thenStatus200()
            throws Exception {

        mvc.perform(get("/exchange-date?date=2021-02-05&from=CAD&to=HKD")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenCurrenciesForNonExistingDate_when_ExchangeDate_thenStatus404()
            throws Exception {

        mvc.perform(get("/exchange-date?date=2021-12-05&from=CAD&to=HKD")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenCurrenciesForExistingDate_when_ExchangeDate_thenStatus201()
            throws Exception {

        mvc.perform(put("/exchange")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

}
