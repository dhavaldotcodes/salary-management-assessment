package com.example.demo.employee;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrgCatalogTest {

    @Test
    void indiaUsesInrAndKnownLevelsAreAccepted() {
        assertEquals("INR", OrgCatalog.currencyFor("in"));
        assertTrue(OrgCatalog.isKnownJobLevel("L3"));
        assertTrue(OrgCatalog.isKnownCountry("US"));
    }

    @Test
    void unknownValuesAreRejected() {
        assertFalse(OrgCatalog.isKnownCountry("XX"));
        assertFalse(OrgCatalog.isKnownJobLevel("L7"));
        assertFalse(OrgCatalog.isKnownJobLevel(null));
    }
}
