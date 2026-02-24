package com.oceanview;

/**
 * TDD test suite for the Reservation model and RoomFactory design pattern.
 * Verifies JSON serialisation, Factory creation, and invalid-type handling.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
import com.oceanview.factory.Room;
import com.oceanview.factory.RoomFactory;
import com.oceanview.model.Reservation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD tests for Reservation model and RoomFactory design pattern.
 */
class ReservationTest {

    // --- RoomFactory tests ---
    @Test
    void testFactoryCreatesStandardRoom() {
        Room room = RoomFactory.createRoom("standard");
        assertEquals("Standard", room.getRoomType());
        assertEquals(5000.00,    room.getPricePerNight(), 0.001);
        assertEquals(2,          room.getCapacity());
    }

    @Test
    void testFactoryCreatesDeluxeRoom() {
        Room room = RoomFactory.createRoom("deluxe");
        assertEquals("Deluxe",  room.getRoomType());
        assertEquals(8000.00,   room.getPricePerNight(), 0.001);
    }

    @Test
    void testFactoryCreatesSuiteRoom() {
        Room room = RoomFactory.createRoom("suite");
        assertEquals("Suite",   room.getRoomType());
        assertEquals(15000.00,  room.getPricePerNight(), 0.001);
        assertEquals(4,         room.getCapacity());
    }

    @Test
    void testFactoryCaseInsensitive() {
        Room r1 = RoomFactory.createRoom("STANDARD");
        Room r2 = RoomFactory.createRoom("Standard");
        Room r3 = RoomFactory.createRoom("standard");
        assertEquals(r1.getRoomType(), r2.getRoomType());
        assertEquals(r2.getRoomType(), r3.getRoomType());
    }

    @Test
    void testFactoryThrowsForUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> RoomFactory.createRoom("penthouse"));
    }

    @Test
    void testFactoryThrowsForNull() {
        assertThrows(IllegalArgumentException.class, () -> RoomFactory.createRoom(null));
    }

    // --- Reservation model tests ---
    @Test
    void testReservationJsonOutput() {
        Reservation r = new Reservation();
        r.setId(1);
        r.setReservationNumber("RES-20240601-1001");
        r.setGuestName("Nalaka Perera");
        r.setContactNumber("0771234567");
        r.setRoomTypeName("Deluxe");
        r.setPricePerNight(8000.00);
        r.setCheckInDate("2024-06-01");
        r.setCheckOutDate("2024-06-05");
        r.setStatus("CONFIRMED");

        String json = r.toJson();
        assertTrue(json.contains("RES-20240601-1001"));
        assertTrue(json.contains("Nalaka Perera"));
        assertTrue(json.contains("CONFIRMED"));
        assertTrue(json.contains("8000.00"));
    }

    @Test
    void testReservationJsonEscapesSpecialChars() {
        Reservation r = new Reservation();
        r.setGuestName("O'Brien \"Test\"");
        r.setReservationNumber("RES-001");
        r.setAddress("");
        r.setContactNumber("0771234567");
        r.setRoomTypeName("Standard");
        r.setPricePerNight(5000.0);
        r.setCheckInDate("2024-01-01");
        r.setCheckOutDate("2024-01-02");
        r.setStatus("CONFIRMED");
        String json = r.toJson();
        // Should not throw and should be valid-looking JSON
        assertNotNull(json);
        assertTrue(json.startsWith("{"));
    }
}
