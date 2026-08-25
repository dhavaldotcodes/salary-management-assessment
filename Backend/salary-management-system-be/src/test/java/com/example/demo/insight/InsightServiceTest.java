package com.example.demo.insight;

import com.example.demo.fx.FxService;
import com.example.demo.insight.dto.GroupStat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightServiceTest {

    @Mock
    private InsightQueryRepository insightQueryRepository;
    @Mock
    private FxService fxService;

    @InjectMocks
    private InsightService insightService;

    @Test
    void loadExposesSqlTotalsAndFxDisclaimer() {
        when(insightQueryRepository.orgTotals()).thenReturn(new InsightQueryRepository.OrgTotals(
                96L,
                4L,
                new BigDecimal("12000000.00"),
                new BigDecimal("125000.00"),
                new BigDecimal("110000.00")
        ));
        when(insightQueryRepository.groupBy("country")).thenReturn(List.of(
                new GroupStat("US", 40, new BigDecimal("8000000.00"), new BigDecimal("200000.00"), new BigDecimal("180000.00"))
        ));
        when(insightQueryRepository.groupBy("department")).thenReturn(List.of());
        when(insightQueryRepository.groupBy("job_level")).thenReturn(List.of());
        when(fxService.asOfDate()).thenReturn(LocalDate.of(2026, 1, 1));

        var insights = insightService.load();

        assertEquals(96L, insights.activeHeadcount());
        assertEquals(4L, insights.inactiveHeadcount());
        assertEquals(new BigDecimal("12000000.00"), insights.payrollUsd());
        assertEquals(LocalDate.of(2026, 1, 1), insights.fxAsOf());
        assertEquals("US", insights.byCountry().get(0).key());
        assertTrue(insights.disclaimer().contains("static FX"));
    }
}
