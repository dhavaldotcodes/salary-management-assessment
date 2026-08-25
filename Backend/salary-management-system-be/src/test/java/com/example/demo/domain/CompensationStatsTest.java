package com.example.demo.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompensationStatsTest {

    @Test
    void sumAveragesAndMedianOnOddList() {
        List<BigDecimal> amounts = List.of(
                new BigDecimal("100.00"),
                new BigDecimal("200.00"),
                new BigDecimal("300.00")
        );

        assertEquals(new BigDecimal("600.00"), CompensationStats.sum(amounts));
        assertEquals(new BigDecimal("200.00"), CompensationStats.average(amounts));
        assertEquals(new BigDecimal("200.00"), CompensationStats.median(amounts));
    }

    @Test
    void medianOnEvenListUsesMeanOfCentreValues() {
        List<BigDecimal> amounts = List.of(
                new BigDecimal("10.00"),
                new BigDecimal("20.00"),
                new BigDecimal("40.00"),
                new BigDecimal("50.00")
        );

        assertEquals(new BigDecimal("30.00"), CompensationStats.median(amounts));
    }

    @Test
    void emptyCollectionIsZeroNotAnError() {
        assertEquals(new BigDecimal("0.00"), CompensationStats.sum(List.of()));
        assertEquals(new BigDecimal("0.00"), CompensationStats.average(List.of()));
        assertEquals(new BigDecimal("0.00"), CompensationStats.median(List.of()));
    }

    @Test
    void medianDoesNotMutateCallerList() {
        List<BigDecimal> amounts = List.of(new BigDecimal("3"), new BigDecimal("1"), new BigDecimal("2"));
        assertEquals(new BigDecimal("2.00"), CompensationStats.median(amounts));
        assertEquals(new BigDecimal("3"), amounts.get(0));
    }
}
