package com.example.demo.employee;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeCodesTest {

    @Test
    void startsAtOneWhenTheTableIsEmpty() {
        assertEquals("ACME-00001", EmployeeCodes.next(null));
        assertEquals("ACME-00001", EmployeeCodes.next(" "));
    }

    @Test
    void incrementsTheNumericSuffix() {
        assertEquals("ACME-10002", EmployeeCodes.next("ACME-10001"));
    }

    @Test
    void ignoresJunkInsteadOfThrowing() {
        assertEquals("ACME-00001", EmployeeCodes.next("EMP-9"));
    }
}
