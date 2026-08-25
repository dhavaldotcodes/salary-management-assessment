package com.example.demo.employee.dto;

import com.example.demo.domain.FxConverter;
import com.example.demo.employee.Employee;
import com.example.demo.employee.EmploymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String country,
        String department,
        String jobLevel,
        EmploymentStatus status,
        BigDecimal baseSalary,
        String currency,
        BigDecimal baseSalaryUsd,
        BigDecimal bonus,
        BigDecimal bonusUsd,
        BigDecimal totalCompensationUsd,
        LocalDate effectiveDate,
        Instant createdAt,
        Instant updatedAt
) {
    public static EmployeeResponse from(Employee employee, FxConverter fx) {
        BigDecimal bonus = employee.getBonus() == null ? BigDecimal.ZERO : employee.getBonus();
        BigDecimal baseUsd = fx.toUsd(employee.getBaseSalary(), employee.getCurrency());
        BigDecimal bonusUsd = fx.toUsd(bonus, employee.getCurrency());
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getCountry(),
                employee.getDepartment(),
                employee.getJobLevel(),
                employee.getStatus(),
                employee.getBaseSalary(),
                employee.getCurrency(),
                baseUsd,
                bonus,
                bonusUsd,
                baseUsd.add(bonusUsd),
                employee.getEffectiveDate(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}
