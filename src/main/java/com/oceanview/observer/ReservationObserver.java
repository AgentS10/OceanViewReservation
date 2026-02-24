package com.oceanview.observer;

import com.oceanview.model.Reservation;

/**
 * Observer Pattern — Observer interface.
 * Any class interested in reservation events implements this interface.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public interface ReservationObserver {
    void onReservationCreated(Reservation reservation);
    void onReservationUpdated(Reservation reservation);
    void onReservationCancelled(Reservation reservation);
}
