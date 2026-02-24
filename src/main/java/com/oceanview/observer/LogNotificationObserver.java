package com.oceanview.observer;

import com.oceanview.model.Reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Observer Pattern — Concrete observer.
 * Logs reservation events to the console with timestamps.
 * Can be extended to file/email/SMS notifications.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class LogNotificationObserver implements ReservationObserver {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onReservationCreated(Reservation r) {
        log("CREATED", r);
    }

    @Override
    public void onReservationUpdated(Reservation r) {
        log("UPDATED", r);
    }

    @Override
    public void onReservationCancelled(Reservation r) {
        log("CANCELLED", r);
    }

    private void log(String event, Reservation r) {
        System.out.printf("[NOTIFICATION] %s | Event: %-10s | Reservation: %-12s | Guest: %s%n",
            LocalDateTime.now().format(FMT), event,
            r.getReservationNumber(), r.getGuestName());
    }
}
