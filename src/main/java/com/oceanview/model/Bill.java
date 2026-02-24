package com.oceanview.model;

/**
 * Bill model for calculating reservation cost.
 * Computes nightly rate, subtotal, 10% tax, and grand total.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class Bill {
    private String reservationNumber;
    private String guestName;
    private String roomTypeName;
    private double pricePerNight;
    private String checkInDate;
    private String checkOutDate;
    private int numberOfNights;
    private double subtotal;
    private double taxRate;
    private double taxAmount;
    private double totalAmount;
    private String status;

    public Bill() {
        this.taxRate = 0.10; // 10% tax
    }

    public void calculate(int nights, double pricePerNight) {
        this.numberOfNights = nights;
        this.pricePerNight = pricePerNight;
        this.subtotal = nights * pricePerNight;
        this.taxAmount = this.subtotal * this.taxRate;
        this.totalAmount = this.subtotal + this.taxAmount;
    }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getRoomTypeName() { return roomTypeName; }
    public void setRoomTypeName(String roomTypeName) { this.roomTypeName = roomTypeName; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public int getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toJson() {
        return String.format(
            "{\"reservationNumber\":\"%s\",\"guestName\":\"%s\",\"roomTypeName\":\"%s\"," +
            "\"pricePerNight\":%.2f,\"checkInDate\":\"%s\",\"checkOutDate\":\"%s\"," +
            "\"numberOfNights\":%d,\"subtotal\":%.2f,\"taxRate\":%.2f,\"taxAmount\":%.2f," +
            "\"totalAmount\":%.2f,\"status\":\"%s\"}",
            escape(reservationNumber), escape(guestName), escape(roomTypeName),
            pricePerNight, escape(checkInDate), escape(checkOutDate),
            numberOfNights, subtotal, taxRate, taxAmount, totalAmount, escape(status)
        );
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
