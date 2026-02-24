package com.oceanview.factory;

/**
 * Factory Pattern — Abstract product interface.
 * All room types (Standard, Deluxe, Suite) implement this interface.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public interface Room {
    String getRoomType();
    double getPricePerNight();
    String getDescription();
    int getCapacity();

    default String toJson() {
        return String.format(
            "{\"roomType\":\"%s\",\"pricePerNight\":%.2f,\"description\":\"%s\",\"capacity\":%d}",
            getRoomType(), getPricePerNight(), getDescription(), getCapacity()
        );
    }
}
