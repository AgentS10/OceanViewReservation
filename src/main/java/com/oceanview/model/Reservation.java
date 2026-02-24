package com.oceanview.model;

/**
 * Domain model representing a room reservation.
 * Tracks guest details, room type, dates, status transitions
 * (CONFIRMED → CHECKED_IN → CHECKED_OUT / CANCELLED), and billing info.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class Reservation {
    private int id;
    private String reservationNumber;
    private String guestName;
    private String address;
    private String contactNumber;
    private int roomTypeId;
    private String roomTypeName;
    private double pricePerNight;
    private String checkInDate;
    private String checkOutDate;
    private String status;
    private String specialRequests;
    private String createdBy;
    private String createdAt;

    public Reservation() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public int getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(int roomTypeId) { this.roomTypeId = roomTypeId; }

    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"reservationNumber\":\"%s\",\"guestName\":\"%s\"," +
            "\"address\":\"%s\",\"contactNumber\":\"%s\",\"roomTypeId\":%d," +
            "\"roomTypeName\":\"%s\",\"pricePerNight\":%.2f,\"checkInDate\":\"%s\"," +
            "\"checkOutDate\":\"%s\",\"status\":\"%s\",\"specialRequests\":\"%s\"," +
            "\"createdBy\":\"%s\",\"createdAt\":\"%s\"}",
            id,
            escape(reservationNumber), escape(guestName),
            escape(address), escape(contactNumber), roomTypeId,
            escape(roomTypeName), pricePerNight,
            escape(checkInDate), escape(checkOutDate),
            escape(status), escape(specialRequests),
            escape(createdBy), escape(createdAt)
        );
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
