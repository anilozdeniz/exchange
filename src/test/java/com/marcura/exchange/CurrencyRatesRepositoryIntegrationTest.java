package com.marcura.exchange;

import com.marcura.exchange.models.entity.CurrencyRates;
import com.marcura.exchange.repository.CurrencyRatesRepository;
import io.swagger.models.auth.In;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CurrencyRatesRepositoryIntegrationTest {

    @Autowired
    private CurrencyRatesRepository currencyRatesRepository;

    private CurrencyRates expectedRates;

    @Before
    public void setUp()
    {
        expectedRates = new CurrencyRates().builder()
                .id(1L)
                .baseCurrency("USD")
                .currency("CAD")
                .rate(new BigDecimal("1.280480681"))
                .rateDate(LocalDate.parse("2021-02-05"))
                .operationCount(0)
                .build();
    }

    @Test
    public void whenFindByIdThenReturnSavedCurrencyRate()
    {
        // given
        CurrencyRates currencyRates = currencyRatesRepository.saveAndFlush(expectedRates);
        // when
        CurrencyRates  actualRates = currencyRatesRepository.findById(currencyRates.getId()).get();
        //then
        assertThat(expectedRates.getRate()).isEqualTo(actualRates.getRate());
        assertThat(expectedRates.getCurrency()).isEqualTo(actualRates.getCurrency());
        assertThat(expectedRates.getBaseCurrency()).isEqualTo(actualRates.getBaseCurrency());
        assertThat(expectedRates.getRateDate()).isEqualTo(actualRates.getRateDate());
        assertThat(expectedRates.getOperationCount()).isEqualTo(actualRates.getOperationCount());
    }

    @Test
    public void WhenDataFilledWithSqlThenReturnCurrencyRate()
    {
        // given
        Long id = 1L;
        // when
        CurrencyRates  actualRates = currencyRatesRepository.findById(id).get();
        //then
        assertThat(expectedRates.getRate()).isEqualTo(actualRates.getRate());
        assertThat(expectedRates.getCurrency()).isEqualTo(actualRates.getCurrency());
        assertThat(expectedRates.getBaseCurrency()).isEqualTo(actualRates.getBaseCurrency());
        assertThat(expectedRates.getRateDate()).isEqualTo(actualRates.getRateDate());
        assertThat(expectedRates.getOperationCount()).isEqualTo(actualRates.getOperationCount());

    }
}
