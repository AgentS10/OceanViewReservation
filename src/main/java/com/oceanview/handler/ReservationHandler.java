package com.oceanview.handler;

/**
 * HTTP handler for all reservation CRUD endpoints.
 * Supports create, read, update, cancel, check-in and check-out operations.
 * Enforces role-based access: STAFF cannot cancel; STAFF cannot check-out.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomTypeDAO;
import com.oceanview.model.Reservation;
import com.oceanview.model.RoomType;
import com.oceanview.model.User;
import com.oceanview.observer.ReservationNotifier;
import com.oceanview.util.JsonUtil;
import com.oceanview.util.SessionManager;
import com.oceanview.util.ValidationUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles REST endpoints for reservations:
 *   GET    /api/reservations          - list all
 *   POST   /api/reservations          - create
 *   GET    /api/reservations/{num}    - get one
 *   PUT    /api/reservations/{num}    - update
 *   DELETE /api/reservations/{num}    - cancel/delete
 */
public class ReservationHandler extends BaseHandler implements HttpHandler {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomTypeDAO roomTypeDAO       = new RoomTypeDAO();
    private final ReservationNotifier notifier  = ReservationNotifier.getInstance();
    private static final AtomicInteger counter  = new AtomicInteger(1000);
    private static final String BASE = "/api/reservations";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendOptions(exchange); return;
        }

        User user = authenticate(exchange);
        if (user == null) return;

        String method = exchange.getRequestMethod();
        String param  = getPathParam(exchange, BASE);

        if (param == null || param.isEmpty()) {
            if ("GET".equalsIgnoreCase(method))       handleList(exchange);
            else if ("POST".equalsIgnoreCase(method)) handleCreate(exchange, user);
            else sendJson(exchange, 405, JsonUtil.error("Method not allowed"));
        } else {
            // Sub-actions: /{num}/checkin  /{num}/checkout
            if (param.endsWith("/checkin") && "POST".equalsIgnoreCase(method)) {
                handleStatusChange(exchange, param.replace("/checkin", ""), "CHECKED_IN", user);
            } else if (param.endsWith("/checkout") && "POST".equalsIgnoreCase(method)) {
                handleStatusChange(exchange, param.replace("/checkout", ""), "CHECKED_OUT", user);
            } else if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange, param);
            } else if ("PUT".equalsIgnoreCase(method)) {
                handleUpdate(exchange, param, user);
            } else if ("DELETE".equalsIgnoreCase(method)) {
                handleDelete(exchange, param, user);
            } else {
                sendJson(exchange, 405, JsonUtil.error("Method not allowed"));
            }
        }
    }

    private void handleList(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        List<Reservation> list;
        if (query != null && query.startsWith("search=")) {
            String term = query.substring(7);
            list = reservationDAO.findByGuestName(term);
        } else {
            list = reservationDAO.findAll();
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(list.get(i).toJson());
        }
        sb.append("]");
        sendJson(exchange, 200, sb.toString());
    }

    private void handleCreate(HttpExchange exchange, User actor) throws IOException {
        String body = readBody(exchange);
        Map<String, String> p = JsonUtil.parseObject(body);

        String guestName     = p.get("guestName");
        String address       = p.get("address");
        String contactNumber = p.get("contactNumber");
        String roomTypeName  = p.get("roomTypeName");
        String checkIn       = p.get("checkInDate");
        String checkOut      = p.get("checkOutDate");
        String special       = p.getOrDefault("specialRequests", "");

        // Validation
        if (ValidationUtil.isNullOrBlank(guestName)) {
            sendJson(exchange, 400, JsonUtil.error("Guest name is required")); return;
        }
        if (!ValidationUtil.isValidContactNumber(contactNumber)) {
            sendJson(exchange, 400, JsonUtil.error("Invalid contact number")); return;
        }
        if (!ValidationUtil.isValidDate(checkIn) || !ValidationUtil.isValidDate(checkOut)) {
            sendJson(exchange, 400, JsonUtil.error("Invalid date format (use YYYY-MM-DD)")); return;
        }
        if (!ValidationUtil.isCheckOutAfterCheckIn(checkIn, checkOut)) {
            sendJson(exchange, 400, JsonUtil.error("Check-out must be after check-in")); return;
        }
        if (ValidationUtil.isNullOrBlank(roomTypeName)) {
            sendJson(exchange, 400, JsonUtil.error("Room type is required")); return;
        }

        RoomType rt = roomTypeDAO.findByName(roomTypeName);
        if (rt == null) {
            sendJson(exchange, 400, JsonUtil.error("Unknown room type: " + roomTypeName)); return;
        }

        String resNum = "RES-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                        + "-" + counter.incrementAndGet();

        Reservation r = new Reservation();
        r.setReservationNumber(resNum);
        r.setGuestName(guestName);
        r.setAddress(address);
        r.setContactNumber(contactNumber);
        r.setRoomTypeId(rt.getId());
        r.setCheckInDate(checkIn);
        r.setCheckOutDate(checkOut);
        r.setStatus("CONFIRMED");
        r.setSpecialRequests(special);
        r.setCreatedBy(actor.getUsername());

        boolean saved = reservationDAO.create(r);
        if (!saved) {
            sendJson(exchange, 500, JsonUtil.error("Failed to create reservation")); return;
        }

        Reservation created = reservationDAO.findByNumber(resNum);
        notifier.notifyCreated(created);
        sendJson(exchange, 201,
            JsonUtil.success("Reservation created successfully", "reservation", created.toJson()));
    }

    private void handleGet(HttpExchange exchange, String resNum) throws IOException {
        Reservation r = reservationDAO.findByNumber(resNum);
        if (r == null) {
            sendJson(exchange, 404, JsonUtil.error("Reservation not found: " + resNum)); return;
        }
        sendJson(exchange, 200, r.toJson());
    }

    private void handleStatusChange(HttpExchange exchange, String resNum, String newStatus, User actor) throws IOException {
        Reservation existing = reservationDAO.findByNumber(resNum);
        if (existing == null) { sendJson(exchange, 404, JsonUtil.error("Reservation not found")); return; }
        String current = existing.getStatus();
        // Validate allowed transitions
        if (newStatus.equals("CHECKED_IN")  && !"CONFIRMED".equals(current)) {
            sendJson(exchange, 400, JsonUtil.error("Can only check in a CONFIRMED reservation")); return;
        }
        if (newStatus.equals("CHECKED_OUT") && !"CHECKED_IN".equals(current)) {
            sendJson(exchange, 400, JsonUtil.error("Can only check out a CHECKED_IN reservation")); return;
        }
        // STAFF cannot perform checkout (MANAGER/ADMIN only)
        if (newStatus.equals("CHECKED_OUT") && "STAFF".equalsIgnoreCase(actor.getRole())) {
            sendJson(exchange, 403, JsonUtil.error("Only MANAGER or ADMIN can perform checkout")); return;
        }
        reservationDAO.updateStatus(resNum, newStatus);
        Reservation fresh = reservationDAO.findByNumber(resNum);
        notifier.notifyUpdated(fresh);
        sendJson(exchange, 200, JsonUtil.success("Status updated to " + newStatus, "reservation", fresh.toJson()));
    }

    private void handleUpdate(HttpExchange exchange, String resNum, User actor) throws IOException {
        Reservation existing = reservationDAO.findByNumber(resNum);
        if (existing == null) {
            sendJson(exchange, 404, JsonUtil.error("Reservation not found: " + resNum)); return;
        }

        String body = readBody(exchange);
        Map<String, String> p = JsonUtil.parseObject(body);

        if (p.containsKey("guestName"))      existing.setGuestName(p.get("guestName"));
        if (p.containsKey("address"))        existing.setAddress(p.get("address"));
        if (p.containsKey("contactNumber"))  existing.setContactNumber(p.get("contactNumber"));
        if (p.containsKey("checkInDate"))    existing.setCheckInDate(p.get("checkInDate"));
        if (p.containsKey("checkOutDate"))   existing.setCheckOutDate(p.get("checkOutDate"));
        // Only ADMIN/MANAGER can change status via PUT
        if (p.containsKey("status") && !"STAFF".equalsIgnoreCase(actor.getRole()))
            existing.setStatus(p.get("status"));
        if (p.containsKey("specialRequests")) existing.setSpecialRequests(p.get("specialRequests"));
        if (p.containsKey("roomTypeName")) {
            RoomType rt = roomTypeDAO.findByName(p.get("roomTypeName"));
            if (rt != null) existing.setRoomTypeId(rt.getId());
        }

        boolean updated = reservationDAO.update(existing);
        if (!updated) {
            sendJson(exchange, 500, JsonUtil.error("Failed to update reservation")); return;
        }
        Reservation fresh = reservationDAO.findByNumber(resNum);
        notifier.notifyUpdated(fresh);
        sendJson(exchange, 200,
            JsonUtil.success("Reservation updated", "reservation", fresh.toJson()));
    }

    private void handleDelete(HttpExchange exchange, String resNum, User actor) throws IOException {
        // STAFF cannot cancel reservations
        if ("STAFF".equalsIgnoreCase(actor.getRole())) {
            sendJson(exchange, 403, JsonUtil.error("STAFF role cannot cancel reservations")); return;
        }
        Reservation existing = reservationDAO.findByNumber(resNum);
        if (existing == null) {
            sendJson(exchange, 404, JsonUtil.error("Reservation not found: " + resNum)); return;
        }
        if ("CANCELLED".equals(existing.getStatus())) {
            sendJson(exchange, 400, JsonUtil.error("Reservation is already cancelled")); return;
        }
        existing.setStatus("CANCELLED");
        notifier.notifyCancelled(existing);
        reservationDAO.updateStatus(resNum, "CANCELLED");
        sendJson(exchange, 200, JsonUtil.success("Reservation cancelled: " + resNum));
    }
}
