package com.marcura.exchange.repository;

import com.marcura.exchange.models.entity.CurrencyRates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface CurrencyRatesRepository extends JpaRepository<CurrencyRates, Long> {

    CurrencyRates findFirstByCurrencyEqualsOrderByRateDateDesc(String currency);
    CurrencyRates findFirstByCurrencyEqualsAndRateDateEquals(String currency, LocalDate date);
    List<CurrencyRates> findAllByRateDate(LocalDate date);
}
