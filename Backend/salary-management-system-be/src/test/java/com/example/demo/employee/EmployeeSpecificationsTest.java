package com.example.demo.employee;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeSpecificationsTest {

    @Test
    void fullNameSearchSplitsIntoFirstAndLastTokens() {
        assertThat(EmployeeSpecifications.tokens("John Doe")).containsExactly("john", "doe");
        assertThat(EmployeeSpecifications.tokens("  JOHN   DOE  ")).containsExactly("john", "doe");
        assertThat(EmployeeSpecifications.tokens("John+Doe")).containsExactly("john", "doe");
        assertThat(EmployeeSpecifications.tokens("\"John Doe\"")).containsExactly("john", "doe");
    }
}
