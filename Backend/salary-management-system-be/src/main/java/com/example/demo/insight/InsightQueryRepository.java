package com.example.demo.insight;

import com.example.demo.insight.dto.GroupStat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class InsightQueryRepository {

    private static final Set<String> GROUP_COLUMNS = Set.of("country", "department", "job_level");

    @PersistenceContext
    private EntityManager entityManager;

    public OrgTotals orgTotals() {
        Query active = entityManager.createNativeQuery("""
                SELECT
                    COUNT(*) AS active_headcount,
                    COALESCE(SUM((e.base_salary + COALESCE(e.bonus, 0)) * fx.usd_rate), 0) AS payroll_usd,
                    COALESCE(AVG((e.base_salary + COALESCE(e.bonus, 0)) * fx.usd_rate), 0) AS avg_usd,
                    COALESCE(PERCENTILE_CONT(0.5) WITHIN GROUP (
                        ORDER BY (e.base_salary + COALESCE(e.bonus, 0)) * fx.usd_rate
                    ), 0) AS median_usd
                FROM employees e
                INNER JOIN fx_rates fx ON fx.currency = e.currency
                WHERE e.status = 'ACTIVE'
                """);
        Object[] row = (Object[]) active.getSingleResult();
        Query inactive = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM employees WHERE status = 'INACTIVE'"
        );
        return new OrgTotals(
                toLong(row[0]),
                toLong(inactive.getSingleResult()),
                toMoney(row[1]),
                toMoney(row[2]),
                toMoney(row[3])
        );
    }

    public List<GroupStat> groupBy(String column) {
        if (!GROUP_COLUMNS.contains(column)) {
            throw new IllegalArgumentException("Unsupported group column: " + column);
        }
        Query query = entityManager.createNativeQuery("""
                SELECT e.%s AS group_key,
                       COUNT(*) AS headcount,
                       SUM((e.base_salary + COALESCE(e.bonus, 0)) * fx.usd_rate) AS payroll_usd,
                       AVG((e.base_salary + COALESCE(e.bonus, 0)) * fx.usd_rate) AS avg_usd,
                       PERCENTILE_CONT(0.5) WITHIN GROUP (
                           ORDER BY (e.base_salary + COALESCE(e.bonus, 0)) * fx.usd_rate
                       ) AS median_usd
                FROM employees e
                INNER JOIN fx_rates fx ON fx.currency = e.currency
                WHERE e.status = 'ACTIVE'
                GROUP BY e.%s
                ORDER BY payroll_usd DESC
                """.formatted(column, column));
        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        List<GroupStat> stats = new ArrayList<>();
        for (Object[] row : rows) {
            stats.add(new GroupStat(
                    String.valueOf(row[0]),
                    toLong(row[1]),
                    toMoney(row[2]),
                    toMoney(row[3]),
                    toMoney(row[4])
            ));
        }
        return stats;
    }

    public record OrgTotals(
            long activeHeadcount,
            long inactiveHeadcount,
            BigDecimal payrollUsd,
            BigDecimal averageUsd,
            BigDecimal medianUsd
    ) {
    }

    private static long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        return ((Number) value).longValue();
    }

    private static BigDecimal toMoney(Object value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2);
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
    }
}
