package com.oceanview.observer;

import com.oceanview.model.Reservation;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern — Subject (Observable).
 * Singleton. Maintains a list of observers and notifies them on reservation events.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class ReservationNotifier {

    private static ReservationNotifier instance;
    private final List<ReservationObserver> observers = new ArrayList<>();

    private ReservationNotifier() {}

    public static synchronized ReservationNotifier getInstance() {
        if (instance == null) instance = new ReservationNotifier();
        return instance;
    }

    public void addObserver(ReservationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ReservationObserver observer) {
        observers.remove(observer);
    }

    public void notifyCreated(Reservation reservation) {
        for (ReservationObserver o : observers) o.onReservationCreated(reservation);
    }

    public void notifyUpdated(Reservation reservation) {
        for (ReservationObserver o : observers) o.onReservationUpdated(reservation);
    }

    public void notifyCancelled(Reservation reservation) {
        for (ReservationObserver o : observers) o.onReservationCancelled(reservation);
    }
}
