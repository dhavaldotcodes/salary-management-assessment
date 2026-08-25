package com.example.demo.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Portable compensation math. Production insights use SQL; these helpers
 * document the intended definitions and keep tests independent of Postgres.
 */
public final class CompensationStats {

    private static final int MONEY_SCALE = 2;

    private CompensationStats() {
    }

    public static BigDecimal sum(List<BigDecimal> amounts) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal amount : safe(amounts)) {
            total = total.add(amount);
        }
        return total.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal average(List<BigDecimal> amounts) {
        List<BigDecimal> values = safe(amounts);
        if (values.isEmpty()) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE);
        }
        return sum(values).divide(BigDecimal.valueOf(values.size()), MONEY_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * Median of a list of amounts. Even counts use the mean of the two central values.
     */
    public static BigDecimal median(List<BigDecimal> amounts) {
        List<BigDecimal> values = new ArrayList<>(safe(amounts));
        if (values.isEmpty()) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE);
        }
        Collections.sort(values);
        int n = values.size();
        if (n % 2 == 1) {
            return values.get(n / 2).setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
        BigDecimal left = values.get(n / 2 - 1);
        BigDecimal right = values.get(n / 2);
        return left.add(right).divide(BigDecimal.TWO, MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private static List<BigDecimal> safe(List<BigDecimal> amounts) {
        return amounts == null ? List.of() : amounts;
    }
}
