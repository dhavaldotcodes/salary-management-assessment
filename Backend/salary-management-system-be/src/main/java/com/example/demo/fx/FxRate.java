package com.example.demo.fx;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fx_rates")
public class FxRate {

    @Id
    @Column(length = 3)
    private String currency;

    @Column(name = "usd_rate", nullable = false, precision = 18, scale = 8)
    private BigDecimal usdRate;

    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

    public FxRate() {
    }

    public FxRate(String currency, BigDecimal usdRate, LocalDate asOfDate) {
        this.currency = currency;
        this.usdRate = usdRate;
        this.asOfDate = asOfDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getUsdRate() {
        return usdRate;
    }

    public void setUsdRate(BigDecimal usdRate) {
        this.usdRate = usdRate;
    }

    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }
}
