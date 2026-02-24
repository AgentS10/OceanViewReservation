package com.oceanview;

/**
 * TDD test suite for user authentication utilities.
 * Tests SHA-256 password hashing, hash verification, and username/password rules.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
import com.oceanview.util.PasswordUtil;
import com.oceanview.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD tests for password hashing and authentication utilities.
 * These tests do NOT require a database connection.
 */
class UserAuthTest {

    @Test
    void testPasswordHashIsDeterministic() {
        String h1 = PasswordUtil.hash("Admin@123");
        String h2 = PasswordUtil.hash("Admin@123");
        assertEquals(h1, h2);
    }

    @Test
    void testDifferentPasswordsProduceDifferentHashes() {
        String h1 = PasswordUtil.hash("Admin@123");
        String h2 = PasswordUtil.hash("Staff@123");
        assertNotEquals(h1, h2);
    }

    @Test
    void testHashLengthIsSHA256() {
        // SHA-256 produces a 64-character hex string
        String hash = PasswordUtil.hash("testpassword");
        assertEquals(64, hash.length());
    }

    @Test
    void testHashIsLowercase() {
        String hash = PasswordUtil.hash("testpassword");
        assertEquals(hash, hash.toLowerCase());
    }

    @Test
    void testVerifyCorrectPassword() {
        String hash = PasswordUtil.hash("Admin@123");
        assertTrue(PasswordUtil.verify("Admin@123", hash));
    }

    @Test
    void testVerifyWrongPasswordFails() {
        String hash = PasswordUtil.hash("Admin@123");
        assertFalse(PasswordUtil.verify("wrongpassword", hash));
    }

    @Test
    void testKnownAdminPasswordHash() {
        // Hash of "123" used for seeding — verify consistency
        String hash = PasswordUtil.hash("123");
        assertEquals(64, hash.length());
        assertNotNull(hash);
    }

    @Test
    void testValidUsernameAccepted() {
        assertTrue(ValidationUtil.isValidUsername("admin"));
        assertTrue(ValidationUtil.isValidUsername("staff_01"));
    }

    @Test
    void testInvalidUsernameRejected() {
        assertFalse(ValidationUtil.isValidUsername("ab"));
        assertFalse(ValidationUtil.isValidUsername(""));
        assertFalse(ValidationUtil.isValidUsername(null));
    }

    @Test
    void testValidPasswordAccepted() {
        assertTrue(ValidationUtil.isValidPassword("Admin@123"));
        assertTrue(ValidationUtil.isValidPassword("123456"));
    }

    @Test
    void testShortPasswordRejected() {
        assertFalse(ValidationUtil.isValidPassword("12345")); // only 5 chars
        assertFalse(ValidationUtil.isValidPassword(""));
        assertFalse(ValidationUtil.isValidPassword(null));
    }
}
