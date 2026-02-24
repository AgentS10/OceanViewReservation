package com.oceanview;

/**
 * TDD test suite for bill calculation and night-counting logic.
 * Verifies correct pricing, tax computation, and edge-case date handling.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
import com.oceanview.model.Bill;
import com.oceanview.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test class for bill calculation logic.
 * Tests the Bill model's calculate() method and ValidationUtil date helpers.
 */
class BillCalculationTest {

    @Test
    void testStandardRoomBillTwoNights() {
        Bill bill = new Bill();
        bill.calculate(2, 5000.00);

        assertEquals(2,       bill.getNumberOfNights());
        assertEquals(10000.0, bill.getSubtotal(),   0.001);
        assertEquals(1000.0,  bill.getTaxAmount(),  0.001);
        assertEquals(11000.0, bill.getTotalAmount(), 0.001);
    }

    @Test
    void testDeluxeRoomBillFiveNights() {
        Bill bill = new Bill();
        bill.calculate(5, 8000.00);

        assertEquals(5,       bill.getNumberOfNights());
        assertEquals(40000.0, bill.getSubtotal(),    0.001);
        assertEquals(4000.0,  bill.getTaxAmount(),   0.001);
        assertEquals(44000.0, bill.getTotalAmount(), 0.001);
    }

    @Test
    void testSuiteRoomBillOneNight() {
        Bill bill = new Bill();
        bill.calculate(1, 15000.00);

        assertEquals(1,        bill.getNumberOfNights());
        assertEquals(15000.0,  bill.getSubtotal(),    0.001);
        assertEquals(1500.0,   bill.getTaxAmount(),   0.001);
        assertEquals(16500.0,  bill.getTotalAmount(), 0.001);
    }

    @Test
    void testTaxRateIstenPercent() {
        Bill bill = new Bill();
        assertEquals(0.10, bill.getTaxRate(), 0.001);
    }

    @ParameterizedTest
    @CsvSource({"2024-01-01,2024-01-03,2", "2024-06-10,2024-06-15,5", "2024-12-31,2025-01-01,1"})
    void testNightsBetween(String checkIn, String checkOut, long expected) {
        assertEquals(expected, ValidationUtil.nightsBetween(checkIn, checkOut));
    }

    @Test
    void testNightsBetweenSameDateReturnsZero() {
        assertEquals(0, ValidationUtil.nightsBetween("2024-05-01", "2024-05-01"));
    }

    @Test
    void testBillJsonContainsAllFields() {
        Bill bill = new Bill();
        bill.setReservationNumber("RES-20240101-1001");
        bill.setGuestName("John Silva");
        bill.setRoomTypeName("Deluxe");
        bill.setCheckInDate("2024-06-01");
        bill.setCheckOutDate("2024-06-03");
        bill.setStatus("CONFIRMED");
        bill.calculate(2, 8000.00);

        String json = bill.toJson();
        assertTrue(json.contains("RES-20240101-1001"));
        assertTrue(json.contains("John Silva"));
        assertTrue(json.contains("Deluxe"));
        assertTrue(json.contains("16000.00")); // subtotal
        assertTrue(json.contains("17600.00")); // total
    }
}
