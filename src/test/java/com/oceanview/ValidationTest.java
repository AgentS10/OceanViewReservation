package com.oceanview;

/**
 * TDD test suite for input validation logic.
 * Covers contact numbers, date parsing, username rules, and password strength.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
import com.oceanview.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD Test class for input validation logic.
 */
class ValidationTest {

    // --- Contact number tests ---
    @ParameterizedTest
    @ValueSource(strings = {"+94771234567", "0771234567", "0112345678", "+447911123456"})
    void testValidContactNumbers(String number) {
        assertTrue(ValidationUtil.isValidContactNumber(number));
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "abc", "", "++9477", "12345678901234567"})
    void testInvalidContactNumbers(String number) {
        assertFalse(ValidationUtil.isValidContactNumber(number));
    }

    // --- Date validation tests ---
    @ParameterizedTest
    @ValueSource(strings = {"2024-01-01", "2024-12-31", "2025-06-15"})
    void testValidDates(String date) {
        assertTrue(ValidationUtil.isValidDate(date));
    }

    @ParameterizedTest
    @ValueSource(strings = {"01-01-2024", "2024/01/01", "notadate", ""})
    void testInvalidDates(String date) {
        assertFalse(ValidationUtil.isValidDate(date));
    }

    // --- Check-out after check-in ---
    @Test
    void testCheckOutAfterCheckIn() {
        assertTrue(ValidationUtil.isCheckOutAfterCheckIn("2024-06-01", "2024-06-05"));
    }

    @Test
    void testCheckOutBeforeCheckInFails() {
        assertFalse(ValidationUtil.isCheckOutAfterCheckIn("2024-06-05", "2024-06-01"));
    }

    @Test
    void testCheckOutSameDayFails() {
        assertFalse(ValidationUtil.isCheckOutAfterCheckIn("2024-06-01", "2024-06-01"));
    }

    // --- Username tests ---
    @ParameterizedTest
    @ValueSource(strings = {"admin", "staff_01", "JohnDoe"})
    void testValidUsernames(String username) {
        assertTrue(ValidationUtil.isValidUsername(username));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ab", "", "user name", "user@domain"})
    void testInvalidUsernames(String username) {
        assertFalse(ValidationUtil.isValidUsername(username));
    }

    // --- Password tests ---
    @Test
    void testValidPassword() {
        assertTrue(ValidationUtil.isValidPassword("Admin@123"));
    }

    @Test
    void testShortPasswordFails() {
        assertFalse(ValidationUtil.isValidPassword("abc"));
    }

    @Test
    void testNullOrBlankDetection() {
        assertTrue(ValidationUtil.isNullOrBlank(null));
        assertTrue(ValidationUtil.isNullOrBlank(""));
        assertTrue(ValidationUtil.isNullOrBlank("   "));
        assertFalse(ValidationUtil.isNullOrBlank("hello"));
    }
}
