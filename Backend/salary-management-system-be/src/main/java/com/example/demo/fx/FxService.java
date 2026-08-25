package com.example.demo.fx;

import com.example.demo.domain.FxConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FxService {

    private final FxRateRepository fxRateRepository;
    private volatile Snapshot snapshot;

    public FxService(FxRateRepository fxRateRepository) {
        this.fxRateRepository = fxRateRepository;
    }

    @Transactional(readOnly = true)
    public FxConverter converter() {
        return load().converter();
    }

    @Transactional(readOnly = true)
    public LocalDate asOfDate() {
        return load().asOfDate();
    }

    private Snapshot load() {
        Snapshot cached = snapshot;
        if (cached != null) {
            return cached;
        }
        synchronized (this) {
            if (snapshot != null) {
                return snapshot;
            }
            Map<String, BigDecimal> rates = new LinkedHashMap<>();
            LocalDate latest = LocalDate.of(2026, 1, 1);
            for (FxRate rate : fxRateRepository.findAll()) {
                rates.put(rate.getCurrency(), rate.getUsdRate());
                if (rate.getAsOfDate().isAfter(latest)) {
                    latest = rate.getAsOfDate();
                }
            }
            snapshot = new Snapshot(new FxConverter(rates), latest);
            return snapshot;
        }
    }

    private record Snapshot(FxConverter converter, LocalDate asOfDate) {
    }
}
