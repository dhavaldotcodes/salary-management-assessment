package com.example.demo.employee;

import com.example.demo.employee.dto.EmployeeRequest;
import com.example.demo.employee.dto.EmployeeResponse;
import com.example.demo.employee.dto.PageResponse;
import com.example.demo.web.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({EmployeeController.class, ApiExceptionHandler.class})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void listEmployeesReturnsPage() throws Exception {
        when(employeeService.search(any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new PageResponse<>(List.of(sampleResponse()), 1, 1, 0, 25));

        mockMvc.perform(get("/api/employees").param("country", "IN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].employeeCode").value("ACME-00001"));
    }

    @Test
    void missingEmployeeIs404() throws Exception {
        when(employeeService.get(42L)).thenThrow(new EmployeeNotFoundException(42L));

        mockMvc.perform(get("/api/employees/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createRejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "",
                                  "lastName": "Lovelace",
                                  "email": "ada@acme.example",
                                  "country": "US",
                                  "department": "Engineering",
                                  "jobLevel": "L4",
                                  "baseSalary": 90000,
                                  "currency": "USD",
                                  "bonus": 0,
                                  "effectiveDate": "2026-01-01"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPersistsValidBody() throws Exception {
        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Ada",
                                  "lastName": "Lovelace",
                                  "email": "ada.lovelace@acme.example",
                                  "country": "US",
                                  "department": "Engineering",
                                  "jobLevel": "L4",
                                  "baseSalary": 90000,
                                  "currency": "USD",
                                  "bonus": 0,
                                  "effectiveDate": "2026-01-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deactivateUsesPatch() throws Exception {
        EmployeeResponse inactive = new EmployeeResponse(
                1L, "ACME-00001", "Ada", "Lovelace", "ada.lovelace@acme.example",
                "US", "Engineering", "L4", EmploymentStatus.INACTIVE,
                new BigDecimal("90000.00"), "USD", new BigDecimal("90000.00"),
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("90000.00"),
                LocalDate.of(2026, 1, 1), null, null
        );
        when(employeeService.deactivate(eq(1L))).thenReturn(inactive);

        mockMvc.perform(patch("/api/employees/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    private static EmployeeResponse sampleResponse() {
        return new EmployeeResponse(
                1L, "ACME-00001", "Ada", "Lovelace", "ada.lovelace@acme.example",
                "US", "Engineering", "L4", EmploymentStatus.ACTIVE,
                new BigDecimal("90000.00"), "USD", new BigDecimal("90000.00"),
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("90000.00"),
                LocalDate.of(2026, 1, 1), null, null
        );
    }
}
