package com.oceanview.handler;

/**
 * HTTP handler for user management endpoints (ADMIN only).
 * Supports full CRUD on user accounts plus toggle-active and change-password sub-actions.
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
import com.oceanview.util.ValidationUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Full CRUD for users — ADMIN only.
 *   GET    /api/users           list all
 *   POST   /api/users           create
 *   GET    /api/users/{id}      get one
 *   PUT    /api/users/{id}      update
 *   DELETE /api/users/{id}      delete
 *   POST   /api/users/{id}/toggle-active   enable/disable
 *   POST   /api/users/{id}/change-password
 */
public class UserHandler extends BaseHandler implements HttpHandler {

    private final UserDAO userDAO = new UserDAO();
    private static final String BASE = "/api/users";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { sendOptions(exchange); return; }

        // All user-management endpoints require ADMIN role
        User actor = requireRole(exchange, "ADMIN");
        if (actor == null) return;

        String method = exchange.getRequestMethod();
        String param  = getPathParam(exchange, BASE);

        if (param == null || param.isEmpty()) {
            if ("GET".equalsIgnoreCase(method))       handleList(exchange);
            else if ("POST".equalsIgnoreCase(method)) handleCreate(exchange);
            else sendJson(exchange, 405, JsonUtil.error("Method not allowed"));
            return;
        }

        // Sub-actions: /{id}/toggle-active or /{id}/change-password
        if (param.contains("/toggle-active")) {
            int id = parseId(param.replace("/toggle-active", ""));
            if (id < 0) { sendJson(exchange, 400, JsonUtil.error("Invalid user ID")); return; }
            handleToggleActive(exchange, id, actor);
            return;
        }
        if (param.contains("/change-password")) {
            int id = parseId(param.replace("/change-password", ""));
            if (id < 0) { sendJson(exchange, 400, JsonUtil.error("Invalid user ID")); return; }
            handleChangePassword(exchange, id);
            return;
        }

        int id = parseId(param);
        if (id < 0) { sendJson(exchange, 400, JsonUtil.error("Invalid user ID")); return; }

        if ("GET".equalsIgnoreCase(method))       handleGetOne(exchange, id);
        else if ("PUT".equalsIgnoreCase(method))  handleUpdate(exchange, id, actor);
        else if ("DELETE".equalsIgnoreCase(method)) handleDelete(exchange, id, actor);
        else sendJson(exchange, 405, JsonUtil.error("Method not allowed"));
    }

    private void handleList(HttpExchange exchange) throws IOException {
        List<User> users = userDAO.findAll();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(users.get(i).toJson());
        }
        sb.append("]");
        sendJson(exchange, 200, sb.toString());
    }

    private void handleGetOne(HttpExchange exchange, int id) throws IOException {
        User u = userDAO.findById(id);
        if (u == null) { sendJson(exchange, 404, JsonUtil.error("User not found")); return; }
        sendJson(exchange, 200, u.toJson());
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        Map<String, String> p = JsonUtil.parseObject(readBody(exchange));
        String username = p.get("username");
        String password = p.get("password");
        String fullName = p.get("fullName");
        String email    = p.getOrDefault("email", "");
        String role     = p.getOrDefault("role", "STAFF");

        if (!ValidationUtil.isValidUsername(username)) {
            sendJson(exchange, 400, JsonUtil.error("Username must be 3-30 alphanumeric/underscore characters")); return;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            sendJson(exchange, 400, JsonUtil.error("Password must be at least 6 characters")); return;
        }
        if (ValidationUtil.isNullOrBlank(fullName)) {
            sendJson(exchange, 400, JsonUtil.error("Full name is required")); return;
        }
        if (!role.matches("ADMIN|MANAGER|STAFF")) {
            sendJson(exchange, 400, JsonUtil.error("Role must be ADMIN, MANAGER or STAFF")); return;
        }
        if (userDAO.findByUsername(username) != null) {
            sendJson(exchange, 409, JsonUtil.error("Username already exists")); return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setActive(true);

        if (!userDAO.create(user)) {
            sendJson(exchange, 500, JsonUtil.error("Failed to create user")); return;
        }
        User created = userDAO.findByUsername(username);
        sendJson(exchange, 201, JsonUtil.success("User created successfully", "user", created.toJson()));
    }

    private void handleUpdate(HttpExchange exchange, int id, User actor) throws IOException {
        User existing = userDAO.findById(id);
        if (existing == null) { sendJson(exchange, 404, JsonUtil.error("User not found")); return; }

        Map<String, String> p = JsonUtil.parseObject(readBody(exchange));
        if (p.containsKey("fullName") && !ValidationUtil.isNullOrBlank(p.get("fullName")))
            existing.setFullName(p.get("fullName"));
        if (p.containsKey("email"))    existing.setEmail(p.get("email"));
        if (p.containsKey("role") && p.get("role").matches("ADMIN|MANAGER|STAFF"))
            existing.setRole(p.get("role"));

        if (!userDAO.update(existing)) {
            sendJson(exchange, 500, JsonUtil.error("Failed to update user")); return;
        }
        sendJson(exchange, 200, JsonUtil.success("User updated", "user", userDAO.findById(id).toJson()));
    }

    private void handleDelete(HttpExchange exchange, int id, User actor) throws IOException {
        if (actor.getId() == id) {
            sendJson(exchange, 400, JsonUtil.error("Cannot delete your own account")); return;
        }
        User target = userDAO.findById(id);
        if (target == null) { sendJson(exchange, 404, JsonUtil.error("User not found")); return; }
        if (!userDAO.delete(id)) {
            sendJson(exchange, 500, JsonUtil.error("Failed to delete user")); return;
        }
        sendJson(exchange, 200, JsonUtil.success("User deleted: " + target.getUsername()));
    }

    private void handleToggleActive(HttpExchange exchange, int id, User actor) throws IOException {
        if (actor.getId() == id) {
            sendJson(exchange, 400, JsonUtil.error("Cannot deactivate your own account")); return;
        }
        User target = userDAO.findById(id);
        if (target == null) { sendJson(exchange, 404, JsonUtil.error("User not found")); return; }
        boolean newState = !target.isActive();
        userDAO.setActive(id, newState);
        sendJson(exchange, 200, JsonUtil.success("User " + (newState ? "activated" : "deactivated")));
    }

    private void handleChangePassword(HttpExchange exchange, int id) throws IOException {
        Map<String, String> p = JsonUtil.parseObject(readBody(exchange));
        String newPwd = p.get("newPassword");
        if (!ValidationUtil.isValidPassword(newPwd)) {
            sendJson(exchange, 400, JsonUtil.error("Password must be at least 6 characters")); return;
        }
        if (!userDAO.changePassword(id, PasswordUtil.hash(newPwd))) {
            sendJson(exchange, 500, JsonUtil.error("Failed to change password")); return;
        }
        sendJson(exchange, 200, JsonUtil.success("Password changed successfully"));
    }

    private int parseId(String s) {
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return -1; }
    }
}
