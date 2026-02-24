package com.oceanview.handler;

/**
 * HTTP handler for authentication endpoints.
 * Manages login (POST /api/auth/login), logout (POST /api/auth/logout)
 * and session status check (GET /api/auth/status).
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;
import com.oceanview.util.JsonUtil;
import com.oceanview.util.PasswordUtil;
import com.oceanview.util.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

/**
 * Handles all authentication endpoints:
 *   POST /api/auth/login
 *   POST /api/auth/logout
 *   GET  /api/auth/status
 */
public class AuthHandler extends BaseHandler implements HttpHandler {

    private final UserDAO userDAO = new UserDAO();
    private final SessionManager sessions = SessionManager.getInstance();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();

        if ("OPTIONS".equalsIgnoreCase(method)) { sendOptions(exchange); return; }

        if (path.endsWith("/login") && "POST".equalsIgnoreCase(method)) {
            handleLogin(exchange);
        } else if (path.endsWith("/logout") && "POST".equalsIgnoreCase(method)) {
            handleLogout(exchange);
        } else if (path.endsWith("/status") && "GET".equalsIgnoreCase(method)) {
            handleStatus(exchange);
        } else {
            sendJson(exchange, 404, JsonUtil.error("Endpoint not found"));
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        Map<String, String> params = JsonUtil.parseObject(body);

        String username = params.get("username");
        String password = params.get("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            sendJson(exchange, 400, JsonUtil.error("Username and password are required"));
            return;
        }

        String hash = PasswordUtil.hash(password);
        if (!userDAO.authenticate(username, hash)) {
            sendJson(exchange, 401, JsonUtil.error("Invalid username or password"));
            return;
        }

        User user  = userDAO.findByUsername(username);
        String ip    = SessionManager.extractIp(exchange);
        String token = sessions.createSession(user, ip);
        String json = String.format(
            "{\"success\":true,\"message\":\"Login successful\",\"token\":\"%s\",\"user\":%s}",
            token, user.toJson()
        );
        sendJson(exchange, 200, json);
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        String token = SessionManager.extractToken(exchange);
        if (token != null) sessions.invalidate(token);
        sendJson(exchange, 200, JsonUtil.success("Logged out successfully"));
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        String token = SessionManager.extractToken(exchange);
        User user = sessions.getUser(token);
        if (user == null) {
            sendJson(exchange, 401, JsonUtil.error("Not authenticated"));
            return;
        }
        sendJson(exchange, 200,
            "{\"success\":true,\"authenticated\":true,\"user\":" + user.toJson() + "}");
    }
}
