package com.oceanview.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * HTTP handler for the help endpoint.
 * GET /api/help — returns system usage guidelines as JSON.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class HelpHandler extends BaseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendOptions(exchange); return;
        }
        String json = "{"
            + "\"title\":\"Ocean View Resort - Help Guide\","
            + "\"sections\":["
            + "{"
            + "\"heading\":\"1. Login\","
            + "\"content\":\"Enter your staff username and password. Default admin: admin / Admin@123. Default staff: staff / Staff@123.\""
            + "},"
            + "{"
            + "\"heading\":\"2. Add Reservation\","
            + "\"content\":\"Navigate to Add Reservation. Fill in guest name, address, contact number, room type, check-in and check-out dates. Click Save to confirm.\""
            + "},"
            + "{"
            + "\"heading\":\"3. View Reservations\","
            + "\"content\":\"Navigate to Reservations list. Search by guest name or browse all. Click View to see full details.\""
            + "},"
            + "{"
            + "\"heading\":\"4. Calculate Bill\","
            + "\"content\":\"Open a reservation and click Calculate Bill. The system computes nights × room rate + 10% tax and displays the total.\""
            + "},"
            + "{"
            + "\"heading\":\"5. Cancel Reservation\","
            + "\"content\":\"Open a reservation and click Cancel. The status will change to CANCELLED. This action cannot be undone.\""
            + "},"
            + "{"
            + "\"heading\":\"6. Logout\","
            + "\"content\":\"Click Logout in the top navigation bar to securely end your session.\""
            + "}"
            + "]"
            + "}";
        sendJson(exchange, 200, json);
    }
}
