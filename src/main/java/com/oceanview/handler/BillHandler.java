package com.oceanview.handler;

/**
 * HTTP handler for billing endpoint.
 * GET /api/bill/{reservationNumber} — calculates and returns the
 * itemised bill (nights × rate + 10% tax) for a given reservation.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
import com.oceanview.dao.ReservationDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.model.User;
import com.oceanview.util.JsonUtil;
import com.oceanview.util.SessionManager;
import com.oceanview.util.ValidationUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Handles bill calculation:
 *   GET /api/bill/{reservationNumber}
 */
public class BillHandler extends BaseHandler implements HttpHandler {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final SessionManager sessions       = SessionManager.getInstance();
    private static final String BASE            = "/api/bill";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendOptions(exchange); return;
        }

        User user = sessions.getUser(SessionManager.extractToken(exchange));
        if (user == null) {
            sendJson(exchange, 401, JsonUtil.error("Authentication required")); return;
        }

        String method = exchange.getRequestMethod();
        if (!"GET".equalsIgnoreCase(method)) {
            sendJson(exchange, 405, JsonUtil.error("Method not allowed")); return;
        }

        String resNum = getPathParam(exchange, BASE);
        if (resNum == null || resNum.isEmpty()) {
            sendJson(exchange, 400, JsonUtil.error("Reservation number is required")); return;
        }

        Reservation r = reservationDAO.findByNumber(resNum);
        if (r == null) {
            sendJson(exchange, 404, JsonUtil.error("Reservation not found: " + resNum)); return;
        }

        long nights = ValidationUtil.nightsBetween(r.getCheckInDate(), r.getCheckOutDate());
        if (nights <= 0) {
            sendJson(exchange, 400, JsonUtil.error("Invalid stay duration")); return;
        }

        Bill bill = new Bill();
        bill.setReservationNumber(r.getReservationNumber());
        bill.setGuestName(r.getGuestName());
        bill.setRoomTypeName(r.getRoomTypeName());
        bill.setCheckInDate(r.getCheckInDate());
        bill.setCheckOutDate(r.getCheckOutDate());
        bill.setStatus(r.getStatus());
        bill.calculate((int) nights, r.getPricePerNight());

        sendJson(exchange, 200, bill.toJson());
    }
}
