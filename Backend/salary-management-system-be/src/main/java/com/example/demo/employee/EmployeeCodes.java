package com.example.demo.employee;

/**
 * ACME-00001 style codes. The next value comes from the highest existing
 * code, not the database id — sequence gaps must not reuse a visible HR id.
 */
public final class EmployeeCodes {

    private EmployeeCodes() {
    }

    public static String next(String latestCode) {
        int latest = parse(latestCode);
        return format(latest + 1);
    }

    static int parse(String code) {
        if (code == null || code.isBlank()) {
            return 0;
        }
        String trimmed = code.trim().toUpperCase();
        if (!trimmed.startsWith("ACME-")) {
            return 0;
        }
        try {
            return Integer.parseInt(trimmed.substring(5));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public static String format(int number) {
        return "ACME-%05d".formatted(Math.max(number, 1));
    }
}
