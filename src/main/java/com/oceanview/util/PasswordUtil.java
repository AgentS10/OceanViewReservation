package com.oceanview.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility for SHA-256 password hashing and verification.
 * No external library required — uses java.security.MessageDigest.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plainText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static boolean verify(String plainText, String hash) {
        return hash(plainText).equals(hash);
    }
}
