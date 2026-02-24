package com.oceanview.factory;

/**
 * Factory Pattern — Concrete creator.
 * Centralises room object instantiation, decoupling the client from concrete classes.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class RoomFactory {

    public static Room createRoom(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Room type cannot be null or empty");
        }
        switch (type.trim().toLowerCase()) {
            case "standard": return new StandardRoom();
            case "deluxe":   return new DeluxeRoom();
            case "suite":    return new SuiteRoom();
            default: throw new IllegalArgumentException("Unknown room type: " + type);
        }
    }
}
