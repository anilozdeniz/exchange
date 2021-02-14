package com.marcura.exchange.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name= "currency_rates")
public class CurrencyRates {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "currency",length = 3)
    private String currency;

    @Column(name="rate")
    private BigDecimal rate;

    @Column(name = "base_currency",length = 3)
    private String baseCurrency;

    @Column(name = "operation_count")
    private Integer operationCount;

    @Column(name = "rate_date", columnDefinition = "DATE")
    private LocalDate rateDate;


}
