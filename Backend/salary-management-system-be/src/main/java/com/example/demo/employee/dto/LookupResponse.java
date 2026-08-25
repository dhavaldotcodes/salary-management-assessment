package com.example.demo.employee.dto;

import java.util.List;

public record LookupResponse(
        List<String> countries,
        List<String> departments,
        List<String> jobLevels,
        List<String> currencies
) {
}
