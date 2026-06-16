-- =============================================================================
-- MediNova - Smart Healthcare Management System
-- MySQL Database Setup Script
-- Compatible with MySQL 8.x
-- Run this script once before launching the Java application.
-- =============================================================================

-- 1. Create & select the database
-- =============================================================================
DROP DATABASE IF EXISTS medinova;
CREATE DATABASE medinova
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE medinova;

-- =============================================================================
-- 2. ADMINS table
-- =============================================================================
CREATE TABLE admins (
    id       INT          AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- 3. PATIENTS table
-- =============================================================================
CREATE TABLE patients (
    id          INT          AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    age         INT          NOT NULL,
    gender      VARCHAR(10)  NOT NULL,
    phone       VARCHAR(15),
    address     VARCHAR(200),
    blood_group VARCHAR(5),
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- 4. DOCTORS table
-- =============================================================================
CREATE TABLE doctors (
    id               INT          AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    specialization   VARCHAR(100) NOT NULL,
    phone            VARCHAR(15),
    email            VARCHAR(100),
    experience_years INT          DEFAULT 0,
    available_days   VARCHAR(100),
    created_at       TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- 5. APPOINTMENTS table
-- =============================================================================
CREATE TABLE appointments (
    id               INT         AUTO_INCREMENT PRIMARY KEY,
    patient_id       INT         NOT NULL,
    doctor_id        INT         NOT NULL,
    appointment_date DATE        NOT NULL,
    appointment_time TIME        NOT NULL,
    status           ENUM('SCHEDULED','COMPLETED','CANCELLED') DEFAULT 'SCHEDULED',
    notes            TEXT,
    created_at       TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_appt_doctor  FOREIGN KEY (doctor_id)  REFERENCES doctors(id)  ON DELETE CASCADE
);

-- =============================================================================
-- 6. MEDICAL_RECORDS table
-- =============================================================================
CREATE TABLE medical_records (
    id            INT       AUTO_INCREMENT PRIMARY KEY,
    patient_id    INT       NOT NULL,
    doctor_id     INT       NOT NULL,
    record_date   DATE      NOT NULL,
    diagnosis     TEXT,
    prescription  TEXT,
    notes         TEXT,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_mr_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_mr_doctor  FOREIGN KEY (doctor_id)  REFERENCES doctors(id)  ON DELETE CASCADE
);

-- =============================================================================
-- 7. BILLS table
-- =============================================================================
CREATE TABLE bills (
    id               INT           AUTO_INCREMENT PRIMARY KEY,
    patient_id       INT           NOT NULL,
    appointment_id   INT           NOT NULL,
    consultation_fee DECIMAL(10,2) DEFAULT 0.00,
    medicine_cost    DECIMAL(10,2) DEFAULT 0.00,
    test_cost        DECIMAL(10,2) DEFAULT 0.00,
    total_amount     DECIMAL(10,2) DEFAULT 0.00,
    payment_status   ENUM('PAID','PENDING','PARTIAL') DEFAULT 'PENDING',
    bill_date        DATE          NOT NULL,
    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bill_patient      FOREIGN KEY (patient_id)     REFERENCES patients(id)     ON DELETE CASCADE,
    CONSTRAINT fk_bill_appointment  FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

-- =============================================================================
-- 8. SAMPLE DATA
-- =============================================================================

-- Admin credentials  (username: admin | password: admin123)
INSERT INTO admins (username, password) VALUES
    ('admin', 'admin123'),
    ('manager', 'manager@456');

-- Sample Patients
INSERT INTO patients (name, age, gender, phone, address, blood_group) VALUES
    ('Aarav Sharma',    28, 'M', '9876543210', 'Hyderabad, Telangana',  'B+'),
    ('Priya Reddy',     34, 'F', '9876543211', 'Bangalore, Karnataka',  'O+'),
    ('Rahul Mehta',     45, 'M', '9876543212', 'Mumbai, Maharashtra',   'A+'),
    ('Sunita Patel',    52, 'F', '9876543213', 'Ahmedabad, Gujarat',    'AB+'),
    ('Kiran Kumar',     22, 'M', '9876543214', 'Chennai, Tamil Nadu',   'O-'),
    ('Ananya Singh',    38, 'F', '9876543215', 'Delhi, NCR',            'A-'),
    ('Vikram Nair',     61, 'M', '9876543216', 'Kochi, Kerala',         'B-'),
    ('Deepika Joshi',   29, 'F', '9876543217', 'Pune, Maharashtra',     'AB-');

-- Sample Doctors
INSERT INTO doctors (name, specialization, phone, email, experience_years, available_days) VALUES
    ('Dr. Neha Gupta',       'Cardiologist',      '9800001111', 'neha.gupta@medinova.com',    12, 'Mon,Tue,Wed,Thu'),
    ('Dr. Rajesh Iyer',      'Neurologist',       '9800002222', 'rajesh.iyer@medinova.com',   18, 'Mon,Wed,Fri'),
    ('Dr. Sonal Bhatt',      'Orthopedician',     '9800003333', 'sonal.bhatt@medinova.com',    9, 'Tue,Thu,Sat'),
    ('Dr. Amitabh Das',      'General Physician', '9800004444', 'amitabh.das@medinova.com',    5, 'Mon,Tue,Wed,Thu,Fri'),
    ('Dr. Kavita Menon',     'Dermatologist',     '9800005555', 'kavita.menon@medinova.com',   7, 'Wed,Thu,Fri'),
    ('Dr. Suresh Rao',       'Pediatrician',      '9800006666', 'suresh.rao@medinova.com',    15, 'Mon,Tue,Thu'),
    ('Dr. Lakshmi Pillai',   'Gynaecologist',     '9800007777', 'lakshmi.pillai@medinova.com', 20, 'Mon,Wed,Fri,Sat'),
    ('Dr. Prashant Tiwari',  'Oncologist',        '9800008888', 'prashant.tiwari@medinova.com',14, 'Tue,Thu');

-- Sample Appointments
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status, notes) VALUES
    (1, 1, '2026-06-17', '10:00', 'SCHEDULED',  'Routine cardiac check-up'),
    (2, 4, '2026-06-17', '11:30', 'SCHEDULED',  'Fever and cough'),
    (3, 2, '2026-06-18', '09:00', 'SCHEDULED',  'Migraine follow-up'),
    (4, 3, '2026-06-18', '14:00', 'SCHEDULED',  'Knee pain assessment'),
    (5, 6, '2026-06-15', '09:30', 'COMPLETED',  'Child vaccination'),
    (6, 5, '2026-06-14', '15:00', 'COMPLETED',  'Skin allergy consultation'),
    (7, 1, '2026-06-13', '10:30', 'CANCELLED',  'Patient rescheduled'),
    (8, 7, '2026-06-16', '11:00', 'SCHEDULED',  'Prenatal check-up');

-- Sample Medical Records
INSERT INTO medical_records (patient_id, doctor_id, record_date, diagnosis, prescription, notes) VALUES
    (5, 6, '2026-06-15', 'Healthy child, vaccines due',  'MMR, Polio booster',                    'Next visit in 6 months'),
    (6, 5, '2026-06-14', 'Contact dermatitis',           'Hydrocortisone cream 1%, Cetirizine 10mg','Avoid known allergens'),
    (3, 2, '2026-05-20', 'Chronic migraine',             'Sumatriptan 50mg, Amitriptyline 10mg',   'Review in 4 weeks');

-- Sample Bills
INSERT INTO bills (patient_id, appointment_id, consultation_fee, medicine_cost, test_cost, total_amount, payment_status, bill_date) VALUES
    (5, 5, 500.00,  200.00, 0.00,   700.00,  'PAID',    '2026-06-15'),
    (6, 6, 700.00,  350.00, 500.00, 1550.00, 'PAID',    '2026-06-14'),
    (3, 3, 1200.00, 450.00, 800.00, 2450.00, 'PENDING', '2026-06-18');

-- =============================================================================
-- Verification queries (comment out in production)
-- =============================================================================
SELECT 'admins'          AS `table`, COUNT(*) AS rows FROM admins         UNION ALL
SELECT 'patients'        AS `table`, COUNT(*) AS rows FROM patients        UNION ALL
SELECT 'doctors'         AS `table`, COUNT(*) AS rows FROM doctors         UNION ALL
SELECT 'appointments'    AS `table`, COUNT(*) AS rows FROM appointments    UNION ALL
SELECT 'medical_records' AS `table`, COUNT(*) AS rows FROM medical_records UNION ALL
SELECT 'bills'           AS `table`, COUNT(*) AS rows FROM bills;

-- End of setup script