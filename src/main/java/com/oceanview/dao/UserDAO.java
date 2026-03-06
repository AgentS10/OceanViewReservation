package com.oceanview.dao;

import com.oceanview.database.DatabaseConnection;
import com.oceanview.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pattern: All database operations for User.
 * Supports full CRUD, authentication, and role management.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class UserDAO {

    private Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── Read ────────────────────────────────────────────────────

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id,username,password_hash,full_name,email,avatar_url,role,is_active,created_at FROM users ORDER BY id";
        try (Statement st = getConn().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findAll error: " + e.getMessage());
        }
        return list;
    }

    public User findById(int id) {
        String sql = "SELECT id,username,password_hash,full_name,email,avatar_url,role,is_active,created_at FROM users WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findById error: " + e.getMessage());
        }
        return null;
    }

    public User findByUsername(String username) {
        String sql = "SELECT id,username,password_hash,full_name,email,avatar_url,role,is_active,created_at FROM users WHERE username=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return map(rs); }
        } catch (SQLException e) {
            System.err.println("[UserDAO] findByUsername error: " + e.getMessage());
        }
        return null;
    }

    // ── Auth ────────────────────────────────────────────────────

    public boolean authenticate(String username, String passwordHash) {
        User user = findByUsername(username);
        return user != null && user.isActive() && user.getPasswordHash().equals(passwordHash);
    }

    // ── Create ──────────────────────────────────────────────────

    public boolean create(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, is_active) VALUES (?,?,?,?,?,1)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] create error: " + e.getMessage());
            return false;
        }
    }

    // ── Update ──────────────────────────────────────────────────

    public boolean update(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, role=?, is_active=?, avatar_url=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole());
            ps.setInt(4, user.isActive() ? 1 : 0);
            ps.setString(5, user.getAvatarUrl());
            ps.setInt(6, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] update error: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(int userId, String newHash) {
        String sql = "UPDATE users SET password_hash=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] changePassword error: " + e.getMessage());
            return false;
        }
    }

    public boolean setActive(int id, boolean active) {
        String sql = "UPDATE users SET is_active=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] setActive error: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAvatar(int userId, String avatarUrl) {
        String sql = "UPDATE users SET avatar_url=? WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, avatarUrl);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] updateAvatar error: " + e.getMessage());
            return false;
        }
    }

    // ── Delete ──────────────────────────────────────────────────

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] delete error: " + e.getMessage());
            return false;
        }
    }

    // ── Mapping ─────────────────────────────────────────────────

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getInt("is_active") == 1);
        u.setCreatedAt(rs.getString("created_at"));
        return u;
    }
}
