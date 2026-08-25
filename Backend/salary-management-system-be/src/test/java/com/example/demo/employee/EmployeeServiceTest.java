package com.example.demo.employee;

import com.example.demo.domain.FxConverter;
import com.example.demo.domain.UnknownCurrencyException;
import com.example.demo.employee.dto.EmployeeRequest;
import com.example.demo.employee.dto.EmployeeResponse;
import com.example.demo.fx.FxRateRepository;
import com.example.demo.fx.FxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private FxService fxService;
    @Mock
    private FxRateRepository fxRateRepository;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, fxService, fxRateRepository);
        lenient().when(fxService.converter()).thenReturn(new FxConverter(Map.of(
                "USD", BigDecimal.ONE,
                "INR", new BigDecimal("0.012")
        )));
    }

    @Test
    void createRejectsDuplicateEmail() {
        EmployeeRequest request = sampleRequest("USD", new BigDecimal("90000"));
        when(employeeRepository.existsByEmailIgnoreCase("ada.lovelace@acme.example")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> employeeService.create(request));
    }

    @Test
    void createRejectsUnknownCurrency() {
        EmployeeRequest request = sampleRequest("XXX", new BigDecimal("90000"));
        assertThrows(UnknownCurrencyException.class, () -> employeeService.create(request));
    }

    @Test
    void createAssignsEmployeeCodeAndNormalizesEmail() {
        EmployeeRequest request = sampleRequest("USD", new BigDecimal("90000"));
        when(employeeRepository.existsByEmailIgnoreCase("ada.lovelace@acme.example")).thenReturn(false);
        when(employeeRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(1L);
            return employee;
        });

        EmployeeResponse response = employeeService.create(request);

        assertEquals("ACME-00001", response.employeeCode());
        assertEquals("ada.lovelace@acme.example", response.email());
        assertEquals(new BigDecimal("90000.00"), response.baseSalaryUsd());
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        assertEquals(EmploymentStatus.ACTIVE, captor.getValue().getStatus());
    }

    @Test
    void searchNeverRequestsMoreThan100Rows() {
        when(employeeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        employeeService.search(new EmployeeQuery(null, null, null, null, null), 0, 500, "lastName", "asc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(employeeRepository).findAll(any(Specification.class), captor.capture());
        assertEquals(100, captor.getValue().getPageSize());
    }

    @Test
    void deactivateMarksEmployeeInactive() {
        Employee employee = new Employee();
        employee.setId(9L);
        employee.setEmployeeCode("ACME-00009");
        employee.setFirstName("Ada");
        employee.setLastName("Lovelace");
        employee.setEmail("ada.lovelace@acme.example");
        employee.setCountry("US");
        employee.setDepartment("Engineering");
        employee.setJobLevel("L4");
        employee.setStatus(EmploymentStatus.ACTIVE);
        employee.setBaseSalary(new BigDecimal("90000"));
        employee.setCurrency("USD");
        employee.setBonus(BigDecimal.ZERO);
        employee.setEffectiveDate(LocalDate.of(2026, 1, 1));
        when(employeeRepository.findById(9L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse response = employeeService.deactivate(9L);

        assertEquals(EmploymentStatus.INACTIVE, response.status());
    }

    @Test
    void getMissingEmployeeFails() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.get(99L));
    }

    private static EmployeeRequest sampleRequest(String currency, BigDecimal salary) {
        return new EmployeeRequest(
                "Ada",
                "Lovelace",
                "Ada.Lovelace@acme.example",
                "US",
                "Engineering",
                "L4",
                salary,
                currency,
                BigDecimal.ZERO,
                LocalDate.of(2026, 1, 1),
                null
        );
    }
}
