package com.oceanview.handler;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.User;
import com.oceanview.util.JsonUtil;
import com.oceanview.util.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * HTTP handler for admin-only endpoints (ADMIN and MANAGER roles).
 * <ul>
 *   <li>GET    /api/admin/sessions            list all active sessions</li>
 *   <li>DELETE /api/admin/sessions/{token}    force-terminate a session</li>
 *   <li>GET    /api/admin/stats               dashboard statistics</li>
 * </ul>
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class AdminHandler extends BaseHandler implements HttpHandler {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private static final String BASE = "/api/admin";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { sendOptions(exchange); return; }

        User actor = requireRole(exchange, "ADMIN", "MANAGER");
        if (actor == null) return;

        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (path.startsWith("/api/admin/sessions")) {
            String token = getPathParam(exchange, "/api/admin/sessions");
            if ("DELETE".equalsIgnoreCase(method) && token != null && !token.isEmpty()) {
                handleTerminateSession(exchange, token, actor);
            } else if ("GET".equalsIgnoreCase(method)) {
                handleListSessions(exchange);
            } else {
                sendJson(exchange, 405, JsonUtil.error("Method not allowed"));
            }
        } else if (path.startsWith("/api/admin/stats") && "GET".equalsIgnoreCase(method)) {
            handleStats(exchange);
        } else {
            sendJson(exchange, 404, JsonUtil.error("Endpoint not found"));
        }
    }

    private void handleListSessions(HttpExchange exchange) throws IOException {
        List<SessionManager.SessionInfo> activeSessions = sessions.getActiveSessions();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < activeSessions.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(activeSessions.get(i).toJson());
        }
        sb.append("]");
        sendJson(exchange, 200, sb.toString());
    }

    private void handleTerminateSession(HttpExchange exchange, String token, User actor) throws IOException {
        SessionManager.SessionInfo info = sessions.getSessionInfo(token);
        if (info == null) {
            sendJson(exchange, 404, JsonUtil.error("Session not found or already expired")); return;
        }
        if (info.user.getUsername().equals(actor.getUsername())) {
            sendJson(exchange, 400, JsonUtil.error("Cannot terminate your own session")); return;
        }
        sessions.terminateSession(token);
        sendJson(exchange, 200, JsonUtil.success("Session terminated for user: " + info.user.getUsername()));
    }

    private void handleStats(HttpExchange exchange) throws IOException {
        List<com.oceanview.model.Reservation> all = reservationDAO.findAll();
        long confirmed   = all.stream().filter(r -> "CONFIRMED".equals(r.getStatus())).count();
        long checkedIn   = all.stream().filter(r -> "CHECKED_IN".equals(r.getStatus())).count();
        long checkedOut  = all.stream().filter(r -> "CHECKED_OUT".equals(r.getStatus())).count();
        long cancelled   = all.stream().filter(r -> "CANCELLED".equals(r.getStatus())).count();
        int  activeSess  = sessions.getActiveSessionCount();

        double totalRevenue = all.stream()
            .filter(r -> !"CANCELLED".equals(r.getStatus()))
            .mapToDouble(r -> {
                long nights = com.oceanview.util.ValidationUtil.nightsBetween(r.getCheckInDate(), r.getCheckOutDate());
                return nights * r.getPricePerNight() * 1.10;
            }).sum();

        String json = String.format(
            "{\"totalReservations\":%d,\"confirmed\":%d,\"checkedIn\":%d," +
            "\"checkedOut\":%d,\"cancelled\":%d,\"activeSessions\":%d,\"totalRevenue\":%.2f}",
            all.size(), confirmed, checkedIn, checkedOut, cancelled, activeSess, totalRevenue
        );
        sendJson(exchange, 200, json);
    }
}
