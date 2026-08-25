package com.example.demo.employee.dto;

import com.example.demo.employee.EmploymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @NotBlank @Email @Size(max = 160) String email,
        @NotBlank @Size(min = 2, max = 2) String country,
        @NotBlank @Size(max = 64) String department,
        @NotBlank @Size(max = 8) String jobLevel,
        @NotNull @DecimalMin(value = "0.00", inclusive = false, message = "Base salary must be greater than 0")
        BigDecimal baseSalary,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @DecimalMin(value = "0.00") BigDecimal bonus,
        @NotNull LocalDate effectiveDate,
        EmploymentStatus status
) {
}
