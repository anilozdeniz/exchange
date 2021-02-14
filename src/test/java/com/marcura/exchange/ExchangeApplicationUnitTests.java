package com.marcura.exchange;

import com.marcura.exchange.utils.SpreadEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExchangeApplicationUnitTests {


    @Test
    public void WhenCurrencyFoundThenReturnValue() {

        assertThat(SpreadEnum.getSpreadByCurrency("USD") == 0);
        assertThat(SpreadEnum.getSpreadByCurrency("JPY") == 3);
        assertThat(SpreadEnum.getSpreadByCurrency("HKD") == 3);
        assertThat(SpreadEnum.getSpreadByCurrency("KRW") == 3);
        assertThat(SpreadEnum.getSpreadByCurrency("MYR") == 4);
        assertThat(SpreadEnum.getSpreadByCurrency("INR") == 4);
        assertThat(SpreadEnum.getSpreadByCurrency("MXN") == 4);
        assertThat(SpreadEnum.getSpreadByCurrency("RUB") == 6);
        assertThat(SpreadEnum.getSpreadByCurrency("CNY") == 6);
        assertThat(SpreadEnum.getSpreadByCurrency("ZAR") == 6);

    }

    @Test
    public void WhenCurrencyNotFoundThenReturnDefaultValue() {

        assertThat(SpreadEnum.getSpreadByCurrency("PLN") == 2);
    }

}
