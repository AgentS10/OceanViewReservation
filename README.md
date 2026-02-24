# 🌊 Ocean View Resort — Room Reservation System

| | |
|---|---|
| **Module** | CIS6003 Advanced Programming |
| **Assignment** | WRIT1 — 2025/26 |
| **Author** | Mohamed Subair Mohamed Sajidh |
| **Version** | 2.0 |

---

A fully functional **web-based hotel room reservation system** built with:
- **Backend**: Plain Java (JDK 11) — `com.sun.net.httpserver.HttpServer` (no Spring/framework)
- **Frontend**: Native HTML5, CSS3, JavaScript (no React/Angular/Vue)
- **Database**: MySQL 8 via raw JDBC (no ORM/Hibernate)
- **Testing**: JUnit 5 (TDD) — 5 test classes, 40+ test cases
- **Build**: Apache Maven

---

## 📁 Project Structure

```
OceanViewReservation/
├── docs/                             # UML diagrams (PlantUML)
│   ├── class-diagram.puml
│   ├── use-case-diagram.puml
│   ├── sequence-login.puml
│   └── sequence-create-reservation.puml
├── database/
│   └── schema.sql                    # MySQL schema v2 + seed data
├── src/
│   ├── main/java/com/oceanview/
│   │   ├── Main.java                 # Entry point + shutdown hook
│   │   ├── server/WebServer.java     # Plain Java HTTP server
│   │   ├── handler/                  # MVC Controllers
│   │   │   ├── BaseHandler.java      # RBAC helpers + shared utils
│   │   │   ├── AuthHandler.java      # Login / logout / session status
│   │   │   ├── ReservationHandler.java  # Full CRUD + check-in/out
│   │   │   ├── BillHandler.java      # Bill calculation
│   │   │   ├── RoomHandler.java      # Room type catalogue
│   │   │   ├── HelpHandler.java      # Help guide
│   │   │   ├── UserHandler.java      # User CRUD (ADMIN only)
│   │   │   ├── AdminHandler.java     # Session monitor + stats
│   │   │   └── StaticFileHandler.java
│   │   ├── dao/                      # Data Access Object pattern
│   │   │   ├── UserDAO.java          # Full CRUD + auth
│   │   │   ├── ReservationDAO.java
│   │   │   └── RoomTypeDAO.java
│   │   ├── model/                    # Domain models
│   │   │   ├── User.java             # Roles: ADMIN / MANAGER / STAFF
│   │   │   ├── Reservation.java      # Status: CONFIRMED → CHECKED_IN → CHECKED_OUT
│   │   │   ├── RoomType.java
│   │   │   └── Bill.java
│   │   ├── database/
│   │   │   └── DatabaseConnection.java  # Singleton pattern
│   │   ├── factory/                  # Factory pattern
│   │   │   ├── Room.java             # Product interface
│   │   │   ├── StandardRoom.java
│   │   │   ├── DeluxeRoom.java
│   │   │   ├── SuiteRoom.java
│   │   │   └── RoomFactory.java      # Creator
│   │   ├── observer/                 # Observer pattern
│   │   │   ├── ReservationObserver.java   # Observer interface
│   │   │   ├── ReservationNotifier.java   # Subject (Singleton)
│   │   │   └── LogNotificationObserver.java  # Concrete observer
│   │   └── util/
│   │       ├── JsonUtil.java
│   │       ├── SessionManager.java   # In-memory session store (Singleton)
│   │       ├── PasswordUtil.java     # SHA-256 hashing
│   │       └── ValidationUtil.java
│   └── test/java/com/oceanview/
│       ├── BillCalculationTest.java  # Bill + night-count tests
│       ├── ValidationTest.java       # Input validation tests
│       ├── ReservationTest.java      # Factory pattern + model tests
│       ├── UserAuthTest.java         # Password hashing tests
│       └── SessionAndRbacTest.java   # Session lifecycle + RBAC tests
├── web/                              # Native HTML/CSS/JS frontend
│   ├── index.html                    # Split-hero login page
│   ├── dashboard.html                # Stats + recent reservations
│   ├── add-reservation.html          # Create reservation form
│   ├── reservations.html             # List + search
│   ├── view-reservation.html         # Detail + check-in/out buttons
│   ├── bill.html                     # Invoice / printable bill
│   ├── help.html                     # User guide
│   ├── admin.html                    # Session monitor + stats (ADMIN/MANAGER)
│   ├── users.html                    # User management (ADMIN only)
│   ├── css/style.css                 # v2 design system
│   └── js/
│       ├── api.js                    # Centralised REST client
│       ├── auth.js                   # Auth guard + role-aware navbar
│       ├── dashboard.js
│       ├── reservation.js
│       └── bill.js
└── pom.xml
```

---

## 🏗 Architecture & Design Patterns

### 3-Tier Architecture
| Tier | Technology |
|------|-----------|
| **Presentation** | Native HTML5 / CSS3 / JavaScript (no framework) |
| **Business Logic** | Plain Java handlers + service logic |
| **Data** | MySQL 8 via JDBC (DAO pattern, no ORM) |

### Design Patterns Implemented
| Pattern | Class(es) | Purpose |
|---------|-----------|---------|
| **Singleton** | `DatabaseConnection`, `SessionManager`, `ReservationNotifier` | Controlled single instance |
| **Factory** | `RoomFactory`, `Room`, `StandardRoom`, `DeluxeRoom`, `SuiteRoom` | Decouple room creation from client |
| **Observer** | `ReservationNotifier`, `ReservationObserver`, `LogNotificationObserver` | Event-driven reservation notifications |
| **DAO** | `UserDAO`, `ReservationDAO`, `RoomTypeDAO` | Encapsulate all DB operations |
| **MVC** | Handlers (Controller) + Models + HTML pages (View) | Separation of concerns |

### Role-Based Access Control (RBAC)
| Feature | ADMIN | MANAGER | STAFF |
|---------|:-----:|:-------:|:-----:|
| Create / view reservations | ✅ | ✅ | ✅ |
| Update reservation | ✅ | ✅ | ✅ |
| Check in guest | ✅ | ✅ | ✅ |
| Calculate bill | ✅ | ✅ | ✅ |
| Check out guest | ✅ | ✅ | ❌ |
| Cancel reservation | ✅ | ✅ | ❌ |
| View admin panel & stats | ✅ | ✅ | ❌ |
| Terminate active sessions | ✅ | ❌ | ❌ |
| Manage users (CRUD) | ✅ | ❌ | ❌ |

---

## 📐 UML Diagrams

All diagrams are located in the `docs/` folder in **PlantUML** (`.puml`) format.
Render them using [PlantUML Online Server](https://www.plantuml.com/plantuml/uml/) or the VS Code PlantUML extension.

| File | Diagram |
|------|---------|
| `docs/class-diagram.puml` | Full class diagram with all packages, attributes, methods and relationships |
| `docs/use-case-diagram.puml` | Use case diagram showing actor-role hierarchy and feature access |
| `docs/sequence-login.puml` | Sequence diagram — user login flow with session creation |
| `docs/sequence-create-reservation.puml` | Sequence diagram — reservation creation with Observer notification |

---

## 🚀 Setup & Run

### Prerequisites
- Java JDK 11+
- Apache Maven 3.6+
- MySQL 8.0+

### 1. Database Setup
```sql
-- Run in MySQL Workbench or CLI:
source database/schema.sql
```

### 2. Configure Database Credentials
Edit `src/main/java/com/oceanview/database/DatabaseConnection.java`:
```java
private static final String DB_URL      = "jdbc:mysql://localhost:3306/oceanview_db?...";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "your_password";
```

### 3. Build
```bash
mvn clean package
```

### 4. Run
```bash
# From project root (web/ directory must be in working dir)
java -jar target/ocean-view-reservation-1.0.0-jar-with-dependencies.jar
```

### 5. Open Browser
Navigate to: **http://localhost:8080**

### Default Login Credentials
| Role | Username | Password | Full Name |
|------|----------|----------|-----------|
| Administrator | `admin`   | `Admin@123`   | Mohamed Subair Mohamed Sajidh |
| Manager       | `manager` | `Manager@123` | Ruwan Karunaratne |
| Staff         | `staff`   | `Staff@123`   | Chaminda Perera |
| Staff         | `kavindi` | `Staff@123`   | Kavindi Senanayake |

---

## 🌐 REST API Endpoints

### Authentication
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | None | Authenticate and get session token |
| POST | `/api/auth/logout` | Any | Invalidate session |
| GET  | `/api/auth/status` | Any | Check session validity |

### Reservations
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET    | `/api/reservations` | Any | List all (supports `?search=name`) |
| POST   | `/api/reservations` | Any | Create reservation |
| GET    | `/api/reservations/{num}` | Any | Get by reservation number |
| PUT    | `/api/reservations/{num}` | Any | Update reservation |
| DELETE | `/api/reservations/{num}` | MANAGER+ | Cancel reservation |
| POST   | `/api/reservations/{num}/checkin`  | Any | Mark as CHECKED_IN |
| POST   | `/api/reservations/{num}/checkout` | MANAGER+ | Mark as CHECKED_OUT |

### Admin & Users
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET    | `/api/admin/stats` | MANAGER+ | Dashboard statistics |
| GET    | `/api/admin/sessions` | MANAGER+ | List active sessions |
| DELETE | `/api/admin/sessions/{token}` | ADMIN | Force-terminate session |
| GET    | `/api/users` | ADMIN | List all users |
| GET    | `/api/users/{id}` | ADMIN | Get user by ID |
| POST   | `/api/users` | ADMIN | Create user |
| PUT    | `/api/users/{id}` | ADMIN | Update user |
| DELETE | `/api/users/{id}` | ADMIN | Delete user |
| POST   | `/api/users/{id}/toggle-active` | ADMIN | Activate / deactivate |
| POST   | `/api/users/{id}/change-password` | ADMIN | Reset password |

### Other
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/bill/{num}` | Any | Calculate bill for reservation |
| GET | `/api/rooms` | Any | List room types |
| GET | `/api/help` | Any | System help guide |

---

## 🧪 Running Tests

```bash
mvn test
```

| Test Class | Coverage |
|------------|---------|
| `BillCalculationTest` | Bill calculation, tax computation (10%), night counting |
| `ValidationTest` | Contact numbers, dates, usernames, password strength |
| `ReservationTest` | Factory pattern (all 3 room types), Reservation JSON serialisation |
| `UserAuthTest` | SHA-256 hashing, hash verification, seed hash values |
| `SessionAndRbacTest` | Session lifecycle, RBAC role hierarchy, session info JSON |

---

## ✅ System Functionalities

1. **Multi-Role Authentication** — SHA-256 hashed passwords, UUID session tokens, 8-hour expiry
2. **Role-Based Access Control** — ADMIN / MANAGER / STAFF role hierarchy enforced server-side
3. **Reservation Management** — Full CRUD with server-side validation and search
4. **Status Lifecycle** — CONFIRMED → CHECKED_IN → CHECKED_OUT (or CANCELLED)
5. **Bill Calculation** — Nights × room rate + 10% tax, printable invoice
6. **Active Session Monitoring** — Real-time session list with IP, login time, last-active
7. **User Management** — Full CRUD on user accounts, activate/deactivate, password change
8. **Dashboard Analytics** — Live stats: total bookings, revenue, active sessions
9. **Observer Notifications** — Console logging on reservation events (extendable to email/SMS)
10. **Help Guide** — Inline usage instructions for staff
11. **Responsive UI** — Modern v2 CSS design system with role-aware navigation

---

## 📚 References

- Oracle (2024) *Java SE 11 Documentation — com.sun.net.httpserver*. Available at: https://docs.oracle.com/en/java/
- MySQL (2024) *MySQL 8.0 Reference Manual*. Available at: https://dev.mysql.com/doc/
- JUnit Team (2024) *JUnit 5 User Guide*. Available at: https://junit.org/junit5/docs/current/user-guide/
- Gamma, E., Helm, R., Johnson, R. and Vlissides, J. (1994) *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
- PlantUML (2024) *PlantUML Language Reference Guide*. Available at: https://plantuml.com/guide
