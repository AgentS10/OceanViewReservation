package com.oceanview.factory;

/**
 * Factory Pattern — Concrete product: Deluxe room.
 * Priced at LKR 8,000/night, capacity 2.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class DeluxeRoom implements Room {
    @Override public String getRoomType()      { return "Deluxe"; }
    @Override public double getPricePerNight() { return 8000.00; }
    @Override public String getDescription()   { return "Spacious room with partial sea view, king bed, and premium amenities"; }
    @Override public int    getCapacity()      { return 2; }
}
