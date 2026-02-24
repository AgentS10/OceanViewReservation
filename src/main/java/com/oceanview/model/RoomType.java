package com.oceanview.model;

/**
 * Domain model representing a hotel room type (Standard, Deluxe, Suite).
 * Holds type name, description, price per night, and capacity.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class RoomType {
    private int id;
    private String typeName;
    private String description;
    private double pricePerNight;
    private int capacity;

    public RoomType() {}

    public RoomType(int id, String typeName, String description, double pricePerNight, int capacity) {
        this.id = id;
        this.typeName = typeName;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"typeName\":\"%s\",\"description\":\"%s\",\"pricePerNight\":%.2f,\"capacity\":%d}",
            id, typeName, description, pricePerNight, capacity
        );
    }
}
