package com.oceanview.util;

import com.oceanview.model.User;
import com.sun.net.httpserver.HttpExchange;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Singleton in-memory session store.
 * Tracks active sessions with token, user, IP address, login time and last-active time.
 * Sessions expire after {@code SESSION_TIMEOUT_HOURS} hours of inactivity.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class SessionManager {

    private static SessionManager instance;
    private static final long SESSION_TIMEOUT_MS = 8 * 60 * 60 * 1000L; // 8 hours
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    // ── Session lifecycle ────────────────────────────────────────

    public String createSession(User user, String ipAddress) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new SessionInfo(token, user, ipAddress));
        return token;
    }

    /** Convenience overload when IP is not available. */
    public String createSession(User user) {
        return createSession(user, "unknown");
    }

    public User getUser(String token) {
        if (token == null) return null;
        SessionInfo info = sessions.get(token);
        if (info == null) return null;
        if (System.currentTimeMillis() - info.lastActiveMs > SESSION_TIMEOUT_MS) {
            invalidate(token);
            return null;
        }
        info.touch();
        return info.user;
    }

    public SessionInfo getSessionInfo(String token) {
        if (token == null) return null;
        SessionInfo info = sessions.get(token);
        if (info == null) return null;
        if (System.currentTimeMillis() - info.lastActiveMs > SESSION_TIMEOUT_MS) {
            invalidate(token);
            return null;
        }
        return info;
    }

    public void invalidate(String token) {
        sessions.remove(token);
    }

    public boolean isValid(String token) {
        return getUser(token) != null;
    }

    // ── Active session listing (for admin monitoring) ─────────────

    public List<SessionInfo> getActiveSessions() {
        long now = System.currentTimeMillis();
        // Evict expired first
        sessions.entrySet().removeIf(e -> now - e.getValue().lastActiveMs > SESSION_TIMEOUT_MS);
        return new ArrayList<>(sessions.values());
    }

    public int getActiveSessionCount() {
        return getActiveSessions().size();
    }

    public boolean terminateSession(String token) {
        if (sessions.containsKey(token)) {
            sessions.remove(token);
            return true;
        }
        return false;
    }

    // ── HTTP helpers ─────────────────────────────────────────────

    public static String extractToken(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) return auth.substring(7);
        return null;
    }

    public static String extractIp(HttpExchange exchange) {
        String forwarded = exchange.getRequestHeaders().getFirst("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return exchange.getRemoteAddress().getAddress().getHostAddress();
    }

    // ── SessionInfo inner class ──────────────────────────────────

    public static class SessionInfo {
        public final String token;
        public final User   user;
        public final String ipAddress;
        public final String loginTime;
        public volatile long lastActiveMs;
        public volatile String lastActiveTime;

        SessionInfo(String token, User user, String ipAddress) {
            this.token        = token;
            this.user         = user;
            this.ipAddress    = ipAddress;
            this.loginTime    = LocalDateTime.now().format(FMT);
            this.lastActiveMs = System.currentTimeMillis();
            this.lastActiveTime = this.loginTime;
        }

        void touch() {
            this.lastActiveMs   = System.currentTimeMillis();
            this.lastActiveTime = LocalDateTime.now().format(FMT);
        }

        public String toJson() {
            return String.format(
                "{\"token\":\"%s\",\"userId\":%d,\"username\":\"%s\",\"fullName\":\"%s\"," +
                "\"role\":\"%s\",\"ipAddress\":\"%s\",\"loginTime\":\"%s\",\"lastActive\":\"%s\"}",
                token, user.getId(), user.getUsername(),
                JsonUtil.escape(user.getFullName()), user.getRole(),
                ipAddress, loginTime, lastActiveTime
            );
        }
    }
}
