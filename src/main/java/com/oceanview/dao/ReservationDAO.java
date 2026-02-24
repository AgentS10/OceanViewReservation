package com.oceanview.dao;

import com.oceanview.database.DatabaseConnection;
import com.oceanview.model.Reservation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pattern: All database operations for Reservation are encapsulated here.
 * Supports full CRUD, status transitions, and created_by tracking.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class ReservationDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<Reservation> findAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, rt.type_name, rt.price_per_night FROM reservations r " +
                     "JOIN room_types rt ON r.room_type_id = rt.id ORDER BY r.created_at DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    public Reservation findByNumber(String reservationNumber) {
        String sql = "SELECT r.*, rt.type_name, rt.price_per_night FROM reservations r " +
                     "JOIN room_types rt ON r.room_type_id = rt.id WHERE r.reservation_number = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, reservationNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] findByNumber error: " + e.getMessage());
        }
        return null;
    }

    public Reservation findById(int id) {
        String sql = "SELECT r.*, rt.type_name, rt.price_per_night FROM reservations r " +
                     "JOIN room_types rt ON r.room_type_id = rt.id WHERE r.id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] findById error: " + e.getMessage());
        }
        return null;
    }

    public List<Reservation> findByGuestName(String name) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.*, rt.type_name, rt.price_per_night FROM reservations r " +
                     "JOIN room_types rt ON r.room_type_id = rt.id WHERE r.guest_name LIKE ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] findByGuestName error: " + e.getMessage());
        }
        return list;
    }

    public boolean create(Reservation r) {
        String sql = "INSERT INTO reservations (reservation_number, guest_name, address, contact_number, " +
                     "room_type_id, check_in_date, check_out_date, status, special_requests, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, r.getReservationNumber());
            ps.setString(2, r.getGuestName());
            ps.setString(3, r.getAddress());
            ps.setString(4, r.getContactNumber());
            ps.setInt(5, r.getRoomTypeId());
            ps.setString(6, r.getCheckInDate());
            ps.setString(7, r.getCheckOutDate());
            ps.setString(8, r.getStatus() != null ? r.getStatus() : "CONFIRMED");
            ps.setString(9, r.getSpecialRequests());
            ps.setString(10, r.getCreatedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] create error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(String reservationNumber, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_number = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reservationNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] updateStatus error: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Reservation r) {
        String sql = "UPDATE reservations SET guest_name=?, address=?, contact_number=?, " +
                     "room_type_id=?, check_in_date=?, check_out_date=?, status=?, special_requests=? " +
                     "WHERE reservation_number=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, r.getGuestName());
            ps.setString(2, r.getAddress());
            ps.setString(3, r.getContactNumber());
            ps.setInt(4, r.getRoomTypeId());
            ps.setString(5, r.getCheckInDate());
            ps.setString(6, r.getCheckOutDate());
            ps.setString(7, r.getStatus());
            ps.setString(8, r.getSpecialRequests());
            ps.setString(9, r.getReservationNumber());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String reservationNumber) {
        String sql = "DELETE FROM reservations WHERE reservation_number = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, reservationNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] delete error: " + e.getMessage());
            return false;
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM reservations";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[ReservationDAO] countAll error: " + e.getMessage());
        }
        return 0;
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setReservationNumber(rs.getString("reservation_number"));
        r.setGuestName(rs.getString("guest_name"));
        r.setAddress(rs.getString("address"));
        r.setContactNumber(rs.getString("contact_number"));
        r.setRoomTypeId(rs.getInt("room_type_id"));
        r.setRoomTypeName(rs.getString("type_name"));
        r.setPricePerNight(rs.getDouble("price_per_night"));
        r.setCheckInDate(rs.getString("check_in_date"));
        r.setCheckOutDate(rs.getString("check_out_date"));
        r.setStatus(rs.getString("status"));
        r.setSpecialRequests(rs.getString("special_requests"));
        r.setCreatedBy(rs.getString("created_by"));
        r.setCreatedAt(rs.getString("created_at"));
        return r;
    }
}
