package com.example.demo.insight.dto;

import java.math.BigDecimal;

public record GroupStat(
        String key,
        long headcount,
        BigDecimal payrollUsd,
        BigDecimal averageUsd,
        BigDecimal medianUsd
) {
}
