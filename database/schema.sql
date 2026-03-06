-- Ocean View Resort - Room Reservation System
-- Database Schema v2 — Multi-role, Sessions, Audit Log
-- Run this script in MySQL to set up the database

CREATE DATABASE IF NOT EXISTS oceanview_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE oceanview_db;

-- Users table — roles: ADMIN, MANAGER, STAFF
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    avatar_url VARCHAR(255) DEFAULT NULL,
    role ENUM('ADMIN', 'MANAGER', 'STAFF') DEFAULT 'STAFF',
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Active sessions table — tracks logged-in users
CREATE TABLE IF NOT EXISTS active_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(36) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    username VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    ip_address VARCHAR(45),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Room types table
CREATE TABLE IF NOT EXISTS room_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    price_per_night DECIMAL(10,2) NOT NULL,
    capacity INT DEFAULT 2
);

-- Reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_number VARCHAR(30) NOT NULL UNIQUE,
    guest_name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    contact_number VARCHAR(20) NOT NULL,
    room_type_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status ENUM('CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') DEFAULT 'CONFIRMED',
    special_requests TEXT,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_type_id) REFERENCES room_types(id)
);

-- Audit log — tracks all significant actions
CREATE TABLE IF NOT EXISTS audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    actor_username VARCHAR(50),
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(50),
    target_id VARCHAR(50),
    detail TEXT,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── Seed Data ────────────────────────────────────────────────────

-- Admin: Mohamed Subair Mohamed Sajidh (password: Admin@123)
INSERT IGNORE INTO users (username, password_hash, full_name, email, role) VALUES
('admin',
 'e86f78a8a3caf0b60d8e74e5942aa6d86dc150cd3c03338aef25b7d2d7e3acc7',
 'Mohamed Subair Mohamed Sajidh', 'sajidh@oceanviewresort.lk', 'ADMIN');

-- Manager: Ruwan Karunaratne (password: Manager@123)
INSERT IGNORE INTO users (username, password_hash, full_name, email, role) VALUES
('manager',
 'e8392925a98c9c22795d1fc5d0dfee5b9a6943f6b768ec5a2a0c077e5ed119cf',
 'Ruwan Karunaratne', 'ruwan@oceanviewresort.lk', 'MANAGER');

-- Staff: Chaminda Perera (password: Staff@123)
INSERT IGNORE INTO users (username, password_hash, full_name, email, role) VALUES
('staff',
 'dfd48f36338aa36228ebb9e204bba6b4e18db0b623e25c458901edc831fb18e9',
 'Chaminda Perera', 'chaminda@oceanviewresort.lk', 'STAFF');

-- Extra Staff: Kavindi Senanayake (password: Staff@123)
INSERT IGNORE INTO users (username, password_hash, full_name, email, role) VALUES
('kavindi',
 'dfd48f36338aa36228ebb9e204bba6b4e18db0b623e25c458901edc831fb18e9',
 'Kavindi Senanayake', 'kavindi@oceanviewresort.lk', 'STAFF');

-- Extra Staff: Nilufar Fernando (password: Staff@123)
INSERT IGNORE INTO users (username, password_hash, full_name, email, role) VALUES
('nilufar',
 'dfd48f36338aa36228ebb9e204bba6b4e18db0b623e25c458901edc831fb18e9',
 'Nilufar Fernando', 'nilufar@oceanviewresort.lk', 'STAFF');

-- Room types
INSERT IGNORE INTO room_types (type_name, description, price_per_night, capacity) VALUES
('Standard', 'Comfortable room with garden view, queen bed, and basic amenities', 5000.00, 2),
('Deluxe',   'Spacious room with partial sea view, king bed, and premium amenities', 8000.00, 2),
('Suite',    'Luxurious suite with panoramic ocean view, king bed, and separate living area', 15000.00, 4);

-- Role permissions summary (documentation, not enforced in DB):
-- ADMIN   : full access — users CRUD, session monitoring, all reservations, audit log
-- MANAGER : all reservations CRUD, reports, billing; cannot manage users
-- STAFF   : create/view reservations, calculate bill; cannot cancel/delete or manage users
