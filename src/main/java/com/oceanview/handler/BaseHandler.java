package com.oceanview.handler;

import com.oceanview.model.User;
import com.oceanview.util.JsonUtil;
import com.oceanview.util.SessionManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Abstract base handler providing shared HTTP utility methods and RBAC enforcement.
 * All concrete handlers extend this class to inherit authentication and
 * role-checking helpers ({@code authenticate}, {@code requireRole}, {@code hasRole}).
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public abstract class BaseHandler {

    protected final SessionManager sessions = SessionManager.getInstance();

    // ── Request reading ──────────────────────────────────────────

    protected String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // ── Response writing ─────────────────────────────────────────

    protected void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        addCors(exchange);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    protected void sendOptions(HttpExchange exchange) throws IOException {
        addCors(exchange);
        exchange.sendResponseHeaders(204, -1);
        exchange.getResponseBody().close();
    }

    protected void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type,Authorization");
    }

    // ── RBAC helpers ─────────────────────────────────────────────

    /**
     * Authenticates the request token. Returns the User, or sends 401 and returns null.
     */
    protected User authenticate(HttpExchange exchange) throws IOException {
        User user = sessions.getUser(SessionManager.extractToken(exchange));
        if (user == null) {
            sendJson(exchange, 401, JsonUtil.error("Authentication required"));
        }
        return user;
    }

    /**
     * Authenticates AND checks that the user has one of the required roles.
     * Sends 401/403 and returns null on failure.
     */
    protected User requireRole(HttpExchange exchange, String... roles) throws IOException {
        User user = authenticate(exchange);
        if (user == null) return null;
        boolean ok = Arrays.stream(roles).anyMatch(r -> r.equalsIgnoreCase(user.getRole()));
        if (!ok) {
            sendJson(exchange, 403,
                JsonUtil.error("Access denied. Required role: " + String.join(" or ", roles)));
            return null;
        }
        return user;
    }

    /** True if the authenticated user holds one of the given roles (does NOT send a response). */
    protected boolean hasRole(HttpExchange exchange, String... roles) {
        User user = sessions.getUser(SessionManager.extractToken(exchange));
        if (user == null) return false;
        return Arrays.stream(roles).anyMatch(r -> r.equalsIgnoreCase(user.getRole()));
    }

    // ── Path helpers ─────────────────────────────────────────────

    protected String getPathParam(HttpExchange exchange, String base) {
        String path = exchange.getRequestURI().getPath();
        if (path.length() > base.length()) {
            return path.substring(base.length()).replaceAll("^/+", "");
        }
        return null;
    }
}
