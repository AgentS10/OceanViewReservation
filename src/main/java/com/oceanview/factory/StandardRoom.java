package com.oceanview.factory;

/**
 * Factory Pattern — Concrete product: Standard room.
 * Priced at LKR 5,000/night, capacity 2.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class StandardRoom implements Room {
    @Override public String getRoomType()      { return "Standard"; }
    @Override public double getPricePerNight() { return 5000.00; }
    @Override public String getDescription()   { return "Comfortable room with garden view, queen bed, and basic amenities"; }
    @Override public int    getCapacity()      { return 2; }
}
