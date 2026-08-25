package com.example.demo.insight.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InsightResponse(
        long activeHeadcount,
        long inactiveHeadcount,
        BigDecimal payrollUsd,
        BigDecimal averageCompensationUsd,
        BigDecimal medianCompensationUsd,
        LocalDate fxAsOf,
        String disclaimer,
        List<GroupStat> byCountry,
        List<GroupStat> byDepartment,
        List<GroupStat> byJobLevel
) {
}
