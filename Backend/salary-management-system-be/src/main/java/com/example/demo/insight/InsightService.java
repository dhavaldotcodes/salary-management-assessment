package com.example.demo.insight;

import com.example.demo.fx.FxService;
import com.example.demo.insight.dto.InsightResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InsightService {

    static final String DISCLAIMER =
            "Org-wide totals convert local currency to USD with a static FX table. Treat them as directional, not treasury-grade.";

    private final InsightQueryRepository insightQueryRepository;
    private final FxService fxService;

    public InsightService(InsightQueryRepository insightQueryRepository, FxService fxService) {
        this.insightQueryRepository = insightQueryRepository;
        this.fxService = fxService;
    }

    @Transactional(readOnly = true)
    public InsightResponse load() {
        InsightQueryRepository.OrgTotals totals = insightQueryRepository.orgTotals();
        return new InsightResponse(
                totals.activeHeadcount(),
                totals.inactiveHeadcount(),
                totals.payrollUsd(),
                totals.averageUsd(),
                totals.medianUsd(),
                fxService.asOfDate(),
                DISCLAIMER,
                insightQueryRepository.groupBy("country"),
                insightQueryRepository.groupBy("department"),
                insightQueryRepository.groupBy("job_level")
        );
    }
}
