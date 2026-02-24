package com.oceanview.dao;

import com.oceanview.database.DatabaseConnection;
import com.oceanview.model.RoomType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pattern: All database operations for RoomType.
 * Retrieves room type catalogue from MySQL.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class RoomTypeDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    public List<RoomType> findAll() {
        List<RoomType> list = new ArrayList<>();
        String sql = "SELECT id, type_name, description, price_per_night, capacity FROM room_types";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RoomTypeDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    public RoomType findById(int id) {
        String sql = "SELECT id, type_name, description, price_per_night, capacity FROM room_types WHERE id = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("[RoomTypeDAO] findById error: " + e.getMessage());
        }
        return null;
    }

    public RoomType findByName(String name) {
        String sql = "SELECT id, type_name, description, price_per_night, capacity FROM room_types WHERE type_name = ?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            System.err.println("[RoomTypeDAO] findByName error: " + e.getMessage());
        }
        return null;
    }

    private RoomType map(ResultSet rs) throws SQLException {
        RoomType rt = new RoomType();
        rt.setId(rs.getInt("id"));
        rt.setTypeName(rs.getString("type_name"));
        rt.setDescription(rs.getString("description"));
        rt.setPricePerNight(rs.getDouble("price_per_night"));
        rt.setCapacity(rs.getInt("capacity"));
        return rt;
    }
}
