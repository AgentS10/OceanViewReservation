package com.oceanview.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Input validation helpers used across handlers and the service layer.
 * Validates contact numbers, dates, usernames, passwords, and computes night counts.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class ValidationUtil {

    private ValidationUtil() {}

    public static boolean isNullOrBlank(String s) {
        return s == null || s.isBlank();
    }

    public static boolean isValidContactNumber(String number) {
        if (isNullOrBlank(number)) return false;
        return number.matches("^[+]?[0-9]{7,15}$");
    }

    public static boolean isValidDate(String date) {
        if (isNullOrBlank(date)) return false;
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isCheckOutAfterCheckIn(String checkIn, String checkOut) {
        try {
            LocalDate in  = LocalDate.parse(checkIn);
            LocalDate out = LocalDate.parse(checkOut);
            return out.isAfter(in);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static long nightsBetween(String checkIn, String checkOut) {
        try {
            LocalDate in  = LocalDate.parse(checkIn);
            LocalDate out = LocalDate.parse(checkOut);
            return java.time.temporal.ChronoUnit.DAYS.between(in, out);
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    public static boolean isValidUsername(String username) {
        return !isNullOrBlank(username) && username.matches("^[a-zA-Z0-9_]{3,30}$");
    }

    public static boolean isValidPassword(String password) {
        return !isNullOrBlank(password) && password.length() >= 6;
    }
}
