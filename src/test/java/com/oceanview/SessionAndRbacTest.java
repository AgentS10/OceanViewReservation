package com.oceanview;

/**
 * TDD test suite for SessionManager behaviour and role-based access control (RBAC).
 * Tests in-memory session lifecycle, session expiry logic, and role hierarchy checks.
 * These tests are pure unit tests — no database connection required.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
import com.oceanview.model.User;
import com.oceanview.util.PasswordUtil;
import com.oceanview.util.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SessionManager lifecycle and RBAC role-checking helpers.
 */
class SessionAndRbacTest {

    private SessionManager sm;

    @BeforeEach
    void setUp() throws Exception {
        sm = SessionManager.getInstance();
        // Clear all sessions before each test via reflection
        Field sessionsField = SessionManager.class.getDeclaredField("sessions");
        sessionsField.setAccessible(true);
        ((Map<?, ?>) sessionsField.get(sm)).clear();
    }

    // ── SessionManager tests ─────────────────────────────────────

    @Test
    @DisplayName("createSession returns non-null UUID token")
    void createSession_returnsToken() {
        User user = buildUser("chaminda", "STAFF");
        String token = sm.createSession(user, "127.0.0.1");

        assertNotNull(token, "Token must not be null");
        assertFalse(token.isBlank(), "Token must not be blank");
        assertEquals(36, token.length(), "UUID token should be 36 characters");
    }

    @Test
    @DisplayName("getUser returns correct user for valid token")
    void getUser_validToken_returnsUser() {
        User user = buildUser("ruwan", "MANAGER");
        String token = sm.createSession(user, "192.168.1.10");

        User found = sm.getUser(token);
        assertNotNull(found);
        assertEquals("ruwan", found.getUsername());
        assertEquals("MANAGER", found.getRole());
    }

    @Test
    @DisplayName("getUser returns null for unknown token")
    void getUser_invalidToken_returnsNull() {
        assertNull(sm.getUser("00000000-0000-0000-0000-000000000000"));
    }

    @Test
    @DisplayName("invalidate removes session so getUser returns null")
    void invalidate_removesSession() {
        User user = buildUser("kavindi", "STAFF");
        String token = sm.createSession(user, "10.0.0.1");

        sm.invalidate(token);

        assertNull(sm.getUser(token), "Session should be gone after invalidation");
    }

    @Test
    @DisplayName("terminateSession removes target session")
    void terminateSession_removesSession() {
        User user = buildUser("nilufar", "STAFF");
        String token = sm.createSession(user, "10.0.0.2");

        sm.terminateSession(token);
        assertNull(sm.getUser(token));
    }

    @Test
    @DisplayName("getActiveSessions returns all live sessions")
    void getActiveSessions_returnsAll() {
        sm.createSession(buildUser("admin",   "ADMIN"),   "127.0.0.1");
        sm.createSession(buildUser("ruwan",   "MANAGER"), "127.0.0.2");
        sm.createSession(buildUser("chaminda","STAFF"),   "127.0.0.3");

        assertEquals(3, sm.getActiveSessions().size());
    }

    @Test
    @DisplayName("getSessionInfo returns correct IP address")
    void getSessionInfo_hasCorrectIp() {
        User user = buildUser("admin", "ADMIN");
        String token = sm.createSession(user, "192.168.100.5");

        SessionManager.SessionInfo info = sm.getSessionInfo(token);
        assertNotNull(info);
        assertEquals("192.168.100.5", info.ipAddress);
    }

    @Test
    @DisplayName("getSessionInfo toJson contains required fields")
    void sessionInfo_toJson_containsFields() {
        User user = buildUser("admin", "ADMIN");
        String token = sm.createSession(user, "10.1.2.3");

        String json = sm.getSessionInfo(token).toJson();
        assertTrue(json.contains("\"username\""));
        assertTrue(json.contains("\"role\""));
        assertTrue(json.contains("\"ipAddress\""));
        assertTrue(json.contains("\"loginTime\""));
        assertTrue(json.contains("\"lastActive\""));
    }

    // ── RBAC / role hierarchy tests ──────────────────────────────

    @Test
    @DisplayName("ADMIN role should satisfy ADMIN requirement")
    void rbac_adminSatisfiesAdmin() {
        assertTrue(hasRole(buildUser("admin", "ADMIN"), "ADMIN"));
    }

    @Test
    @DisplayName("ADMIN role should satisfy MANAGER requirement")
    void rbac_adminSatisfiesManager() {
        assertTrue(hasRole(buildUser("admin", "ADMIN"), "MANAGER"));
    }

    @Test
    @DisplayName("ADMIN role should satisfy STAFF requirement")
    void rbac_adminSatisfiesStaff() {
        assertTrue(hasRole(buildUser("admin", "ADMIN"), "STAFF"));
    }

    @Test
    @DisplayName("MANAGER role should NOT satisfy ADMIN requirement")
    void rbac_managerDoesNotSatisfyAdmin() {
        assertFalse(hasRole(buildUser("ruwan", "MANAGER"), "ADMIN"));
    }

    @Test
    @DisplayName("MANAGER role should satisfy MANAGER requirement")
    void rbac_managerSatisfiesManager() {
        assertTrue(hasRole(buildUser("ruwan", "MANAGER"), "MANAGER"));
    }

    @Test
    @DisplayName("STAFF role should NOT satisfy MANAGER requirement")
    void rbac_staffDoesNotSatisfyManager() {
        assertFalse(hasRole(buildUser("chaminda", "STAFF"), "MANAGER"));
    }

    @Test
    @DisplayName("STAFF role should NOT satisfy ADMIN requirement")
    void rbac_staffDoesNotSatisfyAdmin() {
        assertFalse(hasRole(buildUser("chaminda", "STAFF"), "ADMIN"));
    }

    @Test
    @DisplayName("Null role should not satisfy any requirement")
    void rbac_nullRoleFailsAll() {
        User user = buildUser("unknown", null);
        assertFalse(hasRole(user, "STAFF"));
        assertFalse(hasRole(user, "MANAGER"));
        assertFalse(hasRole(user, "ADMIN"));
    }

    // ── Password hash consistency ────────────────────────────────

    @Test
    @DisplayName("Same password always produces same SHA-256 hash")
    void passwordHash_isDeterministic() {
        String h1 = PasswordUtil.hash("Admin@123");
        String h2 = PasswordUtil.hash("Admin@123");
        assertEquals(h1, h2);
    }

    @Test
    @DisplayName("Admin seed password hash matches expected value")
    void adminSeedHash_matchesExpected() {
        String expected = "e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7";
        assertEquals(expected, PasswordUtil.hash("Admin@123"));
    }

    @Test
    @DisplayName("Staff seed password hash matches expected value")
    void staffSeedHash_matchesExpected() {
        String expected = "dfd48f36338aa36228ebb9e204bba6b4e18db0b623e25c458901edc831fb18e9";
        assertEquals(expected, PasswordUtil.hash("Staff@123"));
    }

    // ── Helpers ───────────────────────────────────────────────────

    private User buildUser(String username, String role) {
        User u = new User();
        u.setUsername(username);
        u.setFullName(username);
        u.setRole(role);
        u.setActive(true);
        return u;
    }

    /**
     * Mirrors the role-hierarchy logic in BaseHandler.hasRole().
     * ADMIN >= MANAGER >= STAFF
     */
    private boolean hasRole(User user, String required) {
        if (user == null || user.getRole() == null) return false;
        String r = user.getRole().toUpperCase();
        switch (required.toUpperCase()) {
            case "ADMIN":   return r.equals("ADMIN");
            case "MANAGER": return r.equals("ADMIN") || r.equals("MANAGER");
            case "STAFF":   return r.equals("ADMIN") || r.equals("MANAGER") || r.equals("STAFF");
            default:        return false;
        }
    }
}
