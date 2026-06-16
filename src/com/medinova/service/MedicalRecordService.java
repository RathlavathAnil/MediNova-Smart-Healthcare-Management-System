package com.medinova.service;

import com.medinova.database.DatabaseConnection;

import java.sql.*;
import java.util.Scanner;

/**
 * MedicalRecordService - Handles adding, viewing, and updating medical records
 * linked to patients and doctors.
 */
public class MedicalRecordService {

    private final Connection     connection;
    private final Scanner        scanner;
    private final PatientService patientService;
    private final DoctorService  doctorService;

    private static final String BORDER =
        "+-----+------------+-----------+------------+----------------------+----------------------------+-----------------------------+";
    private static final String HEADER =
        "| ID  | Patient ID | Doctor ID | Date       | Diagnosis            | Prescription               | Notes                       |";

    public MedicalRecordService(Scanner scanner,
                                PatientService patientService,
                                DoctorService  doctorService) {
        this.connection     = DatabaseConnection.getConnection();
        this.scanner        = scanner;
        this.patientService = patientService;
        this.doctorService  = doctorService;
    }

    // =====================================================================
    // ADD MEDICAL RECORD
    // =====================================================================

    public void addMedicalRecord() {
        System.out.println("\n--- Add Medical Record ---");
        System.out.print("Patient ID   : "); int    patientId    = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Doctor ID    : "); int    doctorId     = Integer.parseInt(scanner.nextLine().trim());

        if (!patientService.patientExists(patientId)) {
            System.out.println("[WARN] Patient ID " + patientId + " not found."); return;
        }
        if (!doctorService.doctorExists(doctorId)) {
            System.out.println("[WARN] Doctor ID " + doctorId + " not found."); return;
        }

        System.out.print("Record Date (YYYY-MM-DD): "); String date         = scanner.nextLine().trim();
        System.out.print("Diagnosis               : "); String diagnosis    = scanner.nextLine().trim();
        System.out.print("Prescription            : "); String prescription = scanner.nextLine().trim();
        System.out.print("Additional Notes        : "); String notes        = scanner.nextLine().trim();

        String sql = "INSERT INTO medical_records (patient_id, doctor_id, record_date, diagnosis, prescription, notes) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, patientId);
            ps.setInt   (2, doctorId);
            ps.setString(3, date);
            ps.setString(4, diagnosis);
            ps.setString(5, prescription);
            ps.setString(6, notes);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Medical record added." : "\n[FAIL] Could not add record.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // VIEW ALL MEDICAL RECORDS
    // =====================================================================

    public void viewAllMedicalRecords() {
        System.out.println("\n--- All Medical Records ---");
        String sql = "SELECT * FROM medical_records ORDER BY record_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            printTableHeader();
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                printRow(rs);
            }
            if (!hasRows) System.out.println("| No records found.                                                                                                          |");
            System.out.println(BORDER);
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // VIEW RECORDS BY PATIENT
    // =====================================================================

    public void viewRecordsByPatient() {
        System.out.println("\n--- Medical Records by Patient ---");
        System.out.print("Enter Patient ID: ");
        int patientId = Integer.parseInt(scanner.nextLine().trim());

        String sql = "SELECT * FROM medical_records WHERE patient_id=? ORDER BY record_date DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            printTableHeader();
            boolean found = false;
            while (rs.next()) {
                found = true;
                printRow(rs);
            }
            if (!found) System.out.println("| No records found for Patient ID " + patientId + ".                                                                    |");
            System.out.println(BORDER);
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // UPDATE MEDICAL RECORD
    // =====================================================================

    public void updateMedicalRecord() {
        System.out.println("\n--- Update Medical Record ---");
        System.out.print("Enter Record ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (!recordExists(id)) {
            System.out.println("[WARN] Record ID " + id + " not found.");
            return;
        }

        System.out.print("New Diagnosis    : "); String diagnosis    = scanner.nextLine().trim();
        System.out.print("New Prescription : "); String prescription = scanner.nextLine().trim();
        System.out.print("New Notes        : "); String notes        = scanner.nextLine().trim();

        String sql = "UPDATE medical_records SET diagnosis=?, prescription=?, notes=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, diagnosis);
            ps.setString(2, prescription);
            ps.setString(3, notes);
            ps.setInt   (4, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Medical record updated." : "\n[FAIL] Update failed.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    private boolean recordExists(int id) {
        String sql = "SELECT id FROM medical_records WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    private void printTableHeader() {
        System.out.println(BORDER);
        System.out.println(HEADER);
        System.out.println(BORDER);
    }

    private void printRow(ResultSet rs) throws SQLException {
        System.out.printf("| %-3d | %-10d | %-9d | %-10s | %-20s | %-26s | %-27s |%n",
            rs.getInt("id"),
            rs.getInt("patient_id"),
            rs.getInt("doctor_id"),
            rs.getString("record_date"),
            truncate(rs.getString("diagnosis"),    20),
            truncate(rs.getString("prescription"), 26),
            truncate(rs.getString("notes"),        27));
    }

    /** Truncates a string to the given length for display alignment. */
    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 2) + "..";
    }
}