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

    public FxService(FxRateRepository fxRateRepository) {
        this.fxRateRepository = fxRateRepository;
    }

    @Transactional(readOnly = true)
    public FxConverter converter() {
        Map<String, BigDecimal> rates = new LinkedHashMap<>();
        LocalDate latest = null;
        for (FxRate rate : fxRateRepository.findAll()) {
            rates.put(rate.getCurrency(), rate.getUsdRate());
            if (latest == null || rate.getAsOfDate().isAfter(latest)) {
                latest = rate.getAsOfDate();
            }
        }
        return new FxConverter(rates);
    }

    @Transactional(readOnly = true)
    public LocalDate asOfDate() {
        return fxRateRepository.findAll().stream()
                .map(FxRate::getAsOfDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.of(2026, 1, 1));
    }
}
