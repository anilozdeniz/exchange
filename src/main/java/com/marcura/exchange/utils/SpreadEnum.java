package com.marcura.exchange.utils;

import java.util.HashMap;
import java.util.Map;

public enum SpreadEnum {

    USD("USD",0),
    JPY("JPY",3),
    HKD("HKD",3),
    KRW("KRW",3),
    MYR("MYR",4),
    INR("INR",4),
    MXN("MXN",4),
    RUB("RUB",6),
    CNY("CNY",6),
    ZAR("ZAR",6);

    private String currency;
    private Integer spread;
    private static final Map<String,Integer> spreadMap = new HashMap<>();

    private SpreadEnum(String currency, int spread) {
        this.spread = spread;
        this.currency = currency;
    }

    static {
        for (SpreadEnum s : SpreadEnum.values()) {
            spreadMap.put(s.currency, s.spread);
        }
    }

    public String getCurrency(){
        return currency;
    }
    public Integer getSpread(){
        return spread;
    }

    public static Integer getSpreadByCurrency(String currency){
        return spreadMap.getOrDefault(currency,2);
    }



}



