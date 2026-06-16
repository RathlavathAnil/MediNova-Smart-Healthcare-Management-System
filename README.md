# MediNova – Smart Healthcare Management System

> A fully featured, console-based Java 21 + JDBC + MySQL hospital management system.

---

## Project Structure

```
MediNova/
├── medinova_setup.sql                        ← Run this FIRST in MySQL
├── README.md
└── src/
    └── com/
        └── medinova/
            ├── main/
            │   └── MediNovaApp.java           ← Entry point
            ├── model/
            │   ├── Admin.java
            │   ├── Patient.java
            │   ├── Doctor.java
            │   ├── Appointment.java
            │   ├── MedicalRecord.java
            │   └── Bill.java
            ├── service/
            │   ├── AdminService.java
            │   ├── PatientService.java
            │   ├── DoctorService.java
            │   ├── AppointmentService.java
            │   ├── MedicalRecordService.java
            │   ├── BillingService.java
            │   └── ReportService.java
            └── database/
                └── DatabaseConnection.java    ← Singleton JDBC connection
```

---

## Features

| Module              | Operations                                                     |
|---------------------|----------------------------------------------------------------|
| Admin Login         | Username/password auth (3 attempts)                           |
| Patient Management  | Add · View · Update · Delete · Search (by ID / name)         |
| Doctor Management   | Add · View · Update · Delete · Search (ID / name / specialty)|
| Appointments        | Book · View · Cancel · Reschedule · Check Availability        |
| Medical Records     | Add · View All · View by Patient · Update                     |
| Billing             | Generate Bill · View Bills · View by Patient · Payment Status |
| Reports             | Summary · By Gender · By Specialization · Daily Schedule      |

---

## Prerequisites

| Tool       | Version | Download                                        |
|------------|---------|-------------------------------------------------|
| Java JDK   | 21+     | https://adoptium.net/                           |
| MySQL      | 8.x     | https://dev.mysql.com/downloads/mysql/          |
| Eclipse IDE| 2024+   | https://www.eclipse.org/downloads/              |
| MySQL JDBC | 9.x     | https://dev.mysql.com/downloads/connector/j/    |

---

## Setup Instructions

### Step 1 – Database Setup

1. Open MySQL Workbench (or any MySQL client).
2. Run the setup script:
   ```sql
   SOURCE /path/to/MediNova/medinova_setup.sql;
   ```
3. Confirm tables were created:
   ```sql
   USE medinova;
   SHOW TABLES;
   ```

### Step 2 – Configure Database Credentials

Open `src/com/medinova/database/DatabaseConnection.java` and update:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/medinova";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_mysql_password_here";  // ← change this
```

### Step 3 – Eclipse Project Setup

1. Open Eclipse → **File → New → Java Project**
2. Project name: `MediNova`
3. Set compiler compliance to **Java 21**
4. Right-click project → **Build Path → Add External Archives**
5. Select `mysql-connector-j-x.x.x.jar` from your download
6. Copy or import the `src/` folder contents into the Eclipse project `src/` directory
7. Ensure package declarations match (`com.medinova.main`, etc.)

### Step 4 – Run

1. Open `com/medinova/main/MediNovaApp.java`
2. Right-click → **Run As → Java Application**
3. Log in with:
   - Username: `admin`
   - Password: `admin123`

---

## Default Login Credentials

| Username | Password    |
|----------|-------------|
| admin    | admin123    |
| manager  | manager@456 |

---

## Tech Stack

- **Language**: Java 21
- **Database**: MySQL 8.x
- **Connectivity**: JDBC (PreparedStatement throughout)
- **IDE**: Eclipse
- **Pattern**: Service-layer architecture with Singleton DB connection

---

## Design Patterns Used

- **Singleton** – `DatabaseConnection` (one connection for the entire session)
- **Service Layer** – All business logic in `service/` package; models are POJOs
- **Separation of Concerns** – `model/` (data), `service/` (logic), `database/` (connection), `main/` (UI/menu)

---

## Future Enhancements

1. **Password Hashing** – Replace plain-text passwords with BCrypt or SHA-256
2. **Role-based Access Control** – Separate roles: Admin, Doctor, Receptionist
3. **PDF Bill Generation** – Export bills as PDF using iText or Apache PDFBox
4. **Email Notifications** – Send appointment confirmations via JavaMail API
5. **GUI Front-end** – Migrate console menus to JavaFX or Swing
6. **REST API** – Expose services via Spring Boot for web/mobile clients
7. **Audit Log Table** – Track all inserts/updates/deletes with timestamp and user
8. **Doctor Dashboard** – Doctor-specific login to view their own appointments
9. **Appointment Reminders** – Scheduled jobs (via Quartz) to remind patients
10. **Insurance Integration** – Manage patient insurance details and claim status
11. **Inventory Management** – Track medicine stock linked to prescriptions
12. **Multi-hospital Support** – Extend schema for branch/hospital hierarchy

---

## Troubleshooting

| Problem                          | Solution                                                   |
|----------------------------------|------------------------------------------------------------|
| `ClassNotFoundException` for JDBC| Add `mysql-connector-j.jar` to Eclipse build path          |
| `Access denied for user 'root'`  | Check USERNAME/PASSWORD in `DatabaseConnection.java`       |
| `Unknown database 'medinova'`    | Run `medinova_setup.sql` first                             |
| `Communications link failure`    | Ensure MySQL server is running on port 3306                |
| Scanner input skipping lines     | Always use `scanner.nextLine()` (already done in project)  |

---

*MediNova v1.0 — Built with Java 21 + JDBC + MySQL*
