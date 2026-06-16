package com.medinova.service;

import com.medinova.database.DatabaseConnection;
import com.medinova.model.Patient;

import java.sql.*;
import java.util.Scanner;

/**
 * PatientService - Handles all patient-related database operations.
 * Add / View / Update / Delete / Search patients.
 */
public class PatientService {

    private final Connection connection;
    private final Scanner    scanner;

    // Table border constants
    private static final String BORDER  =
        "+-----+----------------------+-----+--------+--------------+-----------------------+------------+";
    private static final String HEADER  =
        "| ID  | Name                 | Age | Gender | Phone        | Address               | Blood Grp  |";

    public PatientService(Scanner scanner) {
        this.connection = DatabaseConnection.getConnection();
        this.scanner    = scanner;
    }

    // =====================================================================
    // ADD PATIENT
    // =====================================================================

    /**
     * Prompts the user for patient details and inserts a new record.
     */
    public void addPatient() {
        System.out.println("\n--- Add New Patient ---");
        System.out.print("Name        : "); String name       = scanner.nextLine().trim();
        System.out.print("Age         : "); int    age        = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Gender (M/F): "); String gender     = scanner.nextLine().trim();
        System.out.print("Phone       : "); String phone      = scanner.nextLine().trim();
        System.out.print("Address     : "); String address    = scanner.nextLine().trim();
        System.out.print("Blood Group : "); String bloodGroup = scanner.nextLine().trim();

        String sql = "INSERT INTO patients (name, age, gender, phone, address, blood_group) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt   (2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, bloodGroup);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Patient added successfully!" : "\n[FAIL] Could not add patient.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // VIEW ALL PATIENTS
    // =====================================================================

    /**
     * Retrieves and displays all patients in a formatted table.
     */
    public void viewPatients() {
        System.out.println("\n--- Patient List ---");
        String sql = "SELECT * FROM patients ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            printTableHeader();
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                printPatientRow(rs);
            }
            if (!hasRows) System.out.println("| No patients found.                                                                    |");
            System.out.println(BORDER);
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // UPDATE PATIENT
    // =====================================================================

    /**
     * Updates a patient's details by ID.
     */
    public void updatePatient() {
        System.out.println("\n--- Update Patient ---");
        System.out.print("Enter Patient ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (!patientExists(id)) {
            System.out.println("[WARN] Patient with ID " + id + " not found.");
            return;
        }

        System.out.print("New Name        : "); String name       = scanner.nextLine().trim();
        System.out.print("New Age         : "); int    age        = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("New Gender (M/F): "); String gender     = scanner.nextLine().trim();
        System.out.print("New Phone       : "); String phone      = scanner.nextLine().trim();
        System.out.print("New Address     : "); String address    = scanner.nextLine().trim();
        System.out.print("New Blood Group : "); String bloodGroup = scanner.nextLine().trim();

        String sql = "UPDATE patients SET name=?, age=?, gender=?, phone=?, address=?, blood_group=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt   (2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, bloodGroup);
            ps.setInt   (7, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Patient updated successfully!" : "\n[FAIL] Update failed.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // DELETE PATIENT
    // =====================================================================

    /**
     * Deletes a patient record by ID.
     */
    public void deletePatient() {
        System.out.println("\n--- Delete Patient ---");
        System.out.print("Enter Patient ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (!patientExists(id)) {
            System.out.println("[WARN] Patient with ID " + id + " not found.");
            return;
        }

        System.out.print("Confirm delete Patient ID " + id + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        String sql = "DELETE FROM patients WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Patient deleted." : "\n[FAIL] Could not delete patient.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // SEARCH PATIENT
    // =====================================================================

    /**
     * Searches patients by name (partial match) or by ID.
     */
    public void searchPatient() {
        System.out.println("\n--- Search Patient ---");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.print("Choice: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());

        String sql;
        PreparedStatement ps;

        try {
            if (choice == 1) {
                System.out.print("Enter Patient ID: ");
                int id = Integer.parseInt(scanner.nextLine().trim());
                sql = "SELECT * FROM patients WHERE id=?";
                ps  = connection.prepareStatement(sql);
                ps.setInt(1, id);
            } else {
                System.out.print("Enter Name (or part of name): ");
                String name = "%" + scanner.nextLine().trim() + "%";
                sql = "SELECT * FROM patients WHERE name LIKE ?";
                ps  = connection.prepareStatement(sql);
                ps.setString(1, name);
            }

            ResultSet rs = ps.executeQuery();
            printTableHeader();
            boolean found = false;
            while (rs.next()) {
                found = true;
                printPatientRow(rs);
            }
            if (!found) System.out.println("| No matching patients found.                                                           |");
            System.out.println(BORDER);
            ps.close();
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    /**
     * Checks whether a patient with the given ID exists.
     */
    public boolean patientExists(int id) {
        String sql = "SELECT id FROM patients WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    private void printTableHeader() {
        System.out.println(BORDER);
        System.out.println(HEADER);
        System.out.println(BORDER);
    }

    private void printPatientRow(ResultSet rs) throws SQLException {
        System.out.printf("| %-3d | %-20s | %-3d | %-6s | %-12s | %-21s | %-10s |%n",
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("gender"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("blood_group"));
    }
}