package com.oceanview.factory;

/**
 * Factory Pattern — Concrete product: Suite room.
 * Priced at LKR 15,000/night, capacity 4.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class SuiteRoom implements Room {
    @Override public String getRoomType()      { return "Suite"; }
    @Override public double getPricePerNight() { return 15000.00; }
    @Override public String getDescription()   { return "Luxurious suite with panoramic ocean view, king bed, and separate living area"; }
    @Override public int    getCapacity()      { return 4; }
}
