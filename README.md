Ocean View Resort - Room Reservation System

Module: CIS6003 Advanced Programming
Assignment: WRIT1 2025/26
Author: Mohamed Subair Mohamed Sajidh
GitHub: https://github.com/AgentS10/OceanViewReservation

This is my WRIT1 assignment for CIS6003. It is a web-based room reservation system for a hotel called Ocean View Resort in Galle, Sri Lanka. The whole thing is built from scratch using plain Java and native HTML/CSS/JavaScript with no Spring, no React, and no ORMs. The backend runs on Java's built-in HTTP server and connects to a MySQL database through JDBC.

What it does

Staff can log in and create room reservations for guests. Managers can edit, cancel, and check guests in and out. Admins can manage user accounts and monitor who is currently logged in. Any logged-in user can calculate a bill using the formula nights x room rate plus 10% tax and print it. Sessions expire automatically after 8 hours.

Tech used

Backend: Java 11 using com.sun.net.httpserver.HttpServer
Frontend: HTML5, CSS3, plain JavaScript with the fetch API
Database: MySQL 9, raw JDBC with PreparedStatements
Build tool: Apache Maven
Testing: JUnit 5, 76 test cases across 5 test classes

Project structure

    database/schema.sql               - MySQL schema and seed data
    src/main/java/com/oceanview/
        Main.java                     - entry point, starts server on port 8081
        server/WebServer.java         - registers all route handlers
        handler/BaseHandler.java      - shared auth helpers, RBAC enforcement
        handler/AuthHandler.java      - login, logout, session check
        handler/ReservationHandler.java - reservation CRUD and check-in/out
        handler/BillHandler.java      - bill calculation endpoint
        handler/UserHandler.java      - user management, admin only
        handler/AdminHandler.java     - session monitoring and stats
        handler/RoomHandler.java      - room types list
        handler/StaticFileHandler.java - serves the web/ folder
        dao/UserDAO.java              - all SQL for users table
        dao/ReservationDAO.java       - all SQL for reservations table
        dao/RoomTypeDAO.java          - room types queries
        model/User.java
        model/Reservation.java
        model/Bill.java
        model/RoomType.java
        database/DatabaseConnection.java - Singleton JDBC connection
        factory/Room.java             - abstract room product
        factory/StandardRoom.java
        factory/DeluxeRoom.java
        factory/SuiteRoom.java
        factory/RoomFactory.java      - Factory pattern
        observer/ReservationObserver.java - observer interface
        observer/ReservationNotifier.java - subject, Singleton
        observer/LogNotificationObserver.java
        util/SessionManager.java      - in-memory session store, UUID tokens
        util/PasswordUtil.java        - SHA-256 hashing
        util/JsonUtil.java            - manual JSON builder and parser
        util/ValidationUtil.java
    src/test/java/com/oceanview/
        BillCalculationTest.java
        ValidationTest.java
        ReservationTest.java
        UserAuthTest.java
        SessionAndRbacTest.java
    web/
        index.html                    - login page
        dashboard.html
        add-reservation.html
        reservations.html
        view-reservation.html
        bill.html
        users.html                    - admin only
        admin.html                    - admin and manager only
        help.html
        css/style.css
        js/api.js                     - all fetch() calls in one place
        js/auth.js                    - login guard, sidebar, role nav
        js/dashboard.js
        js/reservation.js
        js/bill.js
    pom.xml

Design patterns

I used four design patterns as required by the assignment.

Singleton: DatabaseConnection and SessionManager both use the singleton pattern so there is only ever one database connection and one session store across the whole application.

Factory: RoomFactory.createRoom("deluxe") returns the right room object without the caller needing to know the concrete class name. Adding a new room type only means adding one new class.

Observer: When a reservation is created, updated or cancelled, ReservationNotifier notifies all registered observers. Currently LogNotificationObserver logs the event to the console. You could add an email observer without touching any handler code.

DAO: All SQL is inside UserDAO, ReservationDAO, and RoomTypeDAO. The handler classes never write SQL directly, which makes the code easier to maintain and test.

Role permissions

Action                              ADMIN   MANAGER   STAFF
Create and view reservations        yes     yes       yes
Edit reservation                    yes     yes       yes
Check in guest                      yes     yes       yes
Calculate and print bill            yes     yes       yes
Check out guest                     yes     yes       no
Cancel reservation                  yes     yes       no
View reports and session monitor    yes     yes       no
Terminate a session                 yes     no        no
Manage user accounts                yes     no        no

How to run

Requirements: Java 11, Maven, MySQL 9

Step 1 - Set up the database

    mysql -u root -proot < database/schema.sql

Step 2 - Build

    mvn clean package

Step 3 - Run

    java -jar target/ocean-view-reservation-1.0.0-jar-with-dependencies.jar 8081

Step 4 - Open browser

Go to http://localhost:8081

Login credentials from seed data:

    admin       Admin@123     (ADMIN role)
    manager     Manager@123   (MANAGER role)
    staff       Staff@123     (STAFF role)
    kavindi     Staff@123     (STAFF role)

API endpoints

All endpoints require the header Authorization: Bearer <token> except login.

Auth:
    POST   /api/auth/login         returns session token
    POST   /api/auth/logout        removes session
    GET    /api/auth/status        check if token is still valid

Reservations:
    GET    /api/reservations       list all, supports ?search=name
    POST   /api/reservations       create new
    GET    /api/reservations/{num} get one by reservation number
    PUT    /api/reservations/{num} update
    DELETE /api/reservations/{num} cancel, manager and admin only
    POST   /api/reservations/{num}/checkin   set status to CHECKED_IN
    POST   /api/reservations/{num}/checkout  set status to CHECKED_OUT

Bill and rooms:
    GET    /api/bill/{num}         calculate bill for a reservation
    GET    /api/rooms              list room types and prices

Admin, admin and manager only:
    GET    /api/admin/stats        totals for dashboard
    GET    /api/admin/sessions     list active sessions
    DELETE /api/admin/sessions/{token}  terminate a session, admin only

Users, admin only:
    GET    /api/users              list all accounts
    POST   /api/users              create account
    PUT    /api/users/{id}         update account
    DELETE /api/users/{id}         delete account
    POST   /api/users/{id}/toggle-active    enable or disable account
    POST   /api/users/{id}/change-password  reset password
    POST   /api/users/{id}/avatar           upload profile picture

Running the tests

    mvn test

Expected output: Tests run: 76, Failures: 0, Errors: 0, Skipped: 0

    BillCalculationTest    - bill.calculate() across different night and price combinations, tax at 10%
    ValidationTest         - contact numbers, date format, checkout after checkin, username and password rules
    ReservationTest        - RoomFactory creates correct objects, Reservation toJson output
    UserAuthTest           - SHA-256 hash consistency, verify(), known seed hashes
    SessionAndRbacTest     - session create, get, expire, invalidate, RBAC role hierarchy

Room prices

    Standard   LKR 5,000 per night   capacity 2
    Deluxe     LKR 8,000 per night   capacity 2
    Suite      LKR 15,000 per night  capacity 4

Bill formula: total = (nights x rate) + (nights x rate x 0.10)

References

Oracle (2024) Java SE 11 API Documentation. https://docs.oracle.com/en/java/javase/11/
MySQL (2024) MySQL 8.0 Reference Manual. https://dev.mysql.com/doc/
JUnit Team (2024) JUnit 5 User Guide. https://junit.org/junit5/docs/current/user-guide/
Gamma, E. et al. (1994) Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
