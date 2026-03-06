package com.oceanview.model;

/**
 * Domain model representing a system user.
 * Supports three roles: ADMIN, MANAGER, STAFF.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String role;
    private String avatarUrl;
    private boolean active = true;
    private String createdAt;

    public User() {}

    public User(int id, String username, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\"," +
            "\"avatarUrl\":\"%s\",\"role\":\"%s\",\"active\":%b,\"createdAt\":\"%s\"}",
            id, esc(username), esc(fullName), esc(email),
            esc(avatarUrl), role, active, esc(createdAt)
        );
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
