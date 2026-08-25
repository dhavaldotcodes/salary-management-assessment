package com.example.demo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Objects;

/**
 * Converts a local-currency amount to USD using units-of-USD-per-1-unit-of-currency.
 */
public final class FxConverter {

    private final Map<String, BigDecimal> usdPerUnit;

    public FxConverter(Map<String, BigDecimal> usdPerUnit) {
        this.usdPerUnit = Map.copyOf(usdPerUnit);
    }

    public BigDecimal toUsd(BigDecimal amount, String currency) {
        Objects.requireNonNull(amount, "amount");
        BigDecimal rate = usdPerUnit.get(normalize(currency));
        if (rate == null) {
            throw new UnknownCurrencyException(currency);
        }
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal fromUsd(BigDecimal usdAmount, String currency) {
        Objects.requireNonNull(usdAmount, "usdAmount");
        BigDecimal rate = usdPerUnit.get(normalize(currency));
        if (rate == null) {
            throw new UnknownCurrencyException(currency);
        }
        int scale = "JPY".equals(normalize(currency)) ? 0 : 2;
        return usdAmount.divide(rate, scale, RoundingMode.HALF_UP);
    }

    public boolean supports(String currency) {
        return currency != null && usdPerUnit.containsKey(normalize(currency));
    }

    private static String normalize(String currency) {
        return currency == null ? "" : currency.trim().toUpperCase();
    }
}
