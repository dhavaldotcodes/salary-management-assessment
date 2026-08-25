package com.example.demo.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FxConverterTest {

    private FxConverter converter;

    @BeforeEach
    void setUp() {
        converter = new FxConverter(Map.of(
                "USD", new BigDecimal("1.00000000"),
                "INR", new BigDecimal("0.01200000"),
                "EUR", new BigDecimal("1.08000000"),
                "JPY", new BigDecimal("0.00670000")
        ));
    }

    @Test
    void usdIsUnchanged() {
        assertEquals(new BigDecimal("120000.00"), converter.toUsd(new BigDecimal("120000"), "USD"));
    }

    @Test
    void inrConvertsUsingRate() {
        assertEquals(new BigDecimal("12000.00"), converter.toUsd(new BigDecimal("1000000"), "inr"));
    }

    @Test
    void localAmountCanBeDerivedFromUsd() {
        assertEquals(new BigDecimal("1000000.00"), converter.fromUsd(new BigDecimal("12000.00"), "INR"));
    }

    @Test
    void yenRoundsToWholeUnits() {
        assertEquals(new BigDecimal("149254"), converter.fromUsd(new BigDecimal("1000.00"), "JPY"));
    }

    @Test
    void unknownCurrencyIsRejected() {
        assertThrows(UnknownCurrencyException.class, () -> converter.toUsd(BigDecimal.TEN, "XXX"));
        assertFalse(converter.supports("XXX"));
        assertTrue(converter.supports("eur"));
    }
}
