package com.example.demo.employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeSpecificationsSearchTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void fullNameFindsFirstAndLastTogether() {
        employeeRepository.save(employee("ACME-00001", "John", "Doe", "john.doe@acme.example"));
        employeeRepository.save(employee("ACME-00002", "Jane", "Smith", "jane.smith@acme.example"));

        Page<Employee> page = employeeRepository.findAll(
                EmployeeSpecifications.from(new EmployeeQuery("John Doe", null, null, null, null)),
                PageRequest.of(0, 10)
        );

        assertThat(page.getContent())
                .extracting(Employee::getEmail)
                .containsExactly("john.doe@acme.example");
    }

    @Test
    void reversedFullNameStillMatches() {
        employeeRepository.save(employee("ACME-00003", "Ada", "Lovelace", "ada@acme.example"));

        List<Employee> found = employeeRepository.findAll(
                EmployeeSpecifications.from(new EmployeeQuery("Lovelace Ada", null, null, null, null))
        );

        assertThat(found).extracting(Employee::getFirstName).containsExactly("Ada");
    }

    @Test
    void searchHidesDeactivatedEmployees() {
        Employee inactive = employee("ACME-00004", "John", "Doe", "john.doe@acme.example");
        inactive.setStatus(EmploymentStatus.INACTIVE);
        employeeRepository.save(inactive);

        List<Employee> found = employeeRepository.findAll(
                EmployeeSpecifications.from(new EmployeeQuery("John Doe", null, null, null, null))
        );

        assertThat(found).isEmpty();
    }

    @Test
    void tokensAreSplitOnSpacesAndPluses() {
        assertThat(EmployeeSpecifications.tokens("John Doe")).containsExactly("john", "doe");
        assertThat(EmployeeSpecifications.tokens("John+Doe")).containsExactly("john", "doe");
    }

    private static Employee employee(String code, String first, String last, String email) {
        Employee employee = new Employee();
        employee.setEmployeeCode(code);
        employee.setFirstName(first);
        employee.setLastName(last);
        employee.setEmail(email);
        employee.setCountry("US");
        employee.setDepartment("Engineering");
        employee.setJobLevel("L3");
        employee.setStatus(EmploymentStatus.ACTIVE);
        employee.setBaseSalary(new BigDecimal("10000"));
        employee.setCurrency("USD");
        employee.setBonus(BigDecimal.ZERO);
        employee.setEffectiveDate(LocalDate.of(2026, 1, 1));
        return employee;
    }
}
