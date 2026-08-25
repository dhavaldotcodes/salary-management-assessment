package com.example.demo.employee;

import java.util.Map;
import java.util.Set;

/**
 * Shared ACME org vocabulary so seed data and API validation cannot drift.
 */
public final class OrgCatalog {

    public static final Set<String> JOB_LEVELS = Set.of("L1", "L2", "L3", "L4", "L5", "L6");

    public static final Map<String, String> CURRENCY_BY_COUNTRY = Map.ofEntries(
            Map.entry("US", "USD"),
            Map.entry("IN", "INR"),
            Map.entry("GB", "GBP"),
            Map.entry("DE", "EUR"),
            Map.entry("FR", "EUR"),
            Map.entry("NL", "EUR"),
            Map.entry("IE", "EUR"),
            Map.entry("SG", "SGD"),
            Map.entry("AU", "AUD"),
            Map.entry("CA", "CAD"),
            Map.entry("JP", "JPY"),
            Map.entry("BR", "BRL"),
            Map.entry("CH", "CHF")
    );

    private OrgCatalog() {
    }

    public static boolean isKnownCountry(String country) {
        return country != null && CURRENCY_BY_COUNTRY.containsKey(country.toUpperCase());
    }

    public static boolean isKnownJobLevel(String jobLevel) {
        return jobLevel != null && JOB_LEVELS.contains(jobLevel.toUpperCase());
    }

    public static String currencyFor(String country) {
        if (country == null) {
            return null;
        }
        return CURRENCY_BY_COUNTRY.get(country.toUpperCase());
    }
}
