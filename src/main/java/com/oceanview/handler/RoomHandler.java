package com.oceanview.handler;

import com.oceanview.dao.RoomTypeDAO;
import com.oceanview.model.RoomType;
import com.oceanview.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * HTTP handler for room types endpoint.
 * GET /api/rooms — returns the full catalogue of available room types.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class RoomHandler extends BaseHandler implements HttpHandler {

    private final RoomTypeDAO roomTypeDAO = new RoomTypeDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendOptions(exchange); return;
        }
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJson(exchange, 405, JsonUtil.error("Method not allowed")); return;
        }
        List<RoomType> rooms = roomTypeDAO.findAll();
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rooms.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(rooms.get(i).toJson());
        }
        sb.append("]");
        sendJson(exchange, 200, sb.toString());
    }
}
