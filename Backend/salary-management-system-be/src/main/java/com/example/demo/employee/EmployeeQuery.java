package com.example.demo.employee;

public record EmployeeQuery(
        String search,
        String country,
        String department,
        String jobLevel,
        EmploymentStatus status
) {
}
