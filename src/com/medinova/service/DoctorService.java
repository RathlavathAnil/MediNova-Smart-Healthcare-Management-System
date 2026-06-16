package com.medinova.service;

import com.medinova.database.DatabaseConnection;

import java.sql.*;
import java.util.Scanner;

/**
 * DoctorService - Handles all doctor-related database operations.
 * Add / View / Update / Delete / Search doctors.
 */
public class DoctorService {

    private final Connection connection;
    private final Scanner    scanner;

    private static final String BORDER =
        "+-----+----------------------+-----------------------+-----+--------------+-------------------------+";
    private static final String HEADER =
        "| ID  | Name                 | Specialization        | Exp | Phone        | Available Days          |";

    public DoctorService(Scanner scanner) {
        this.connection = DatabaseConnection.getConnection();
        this.scanner    = scanner;
    }

    // =====================================================================
    // ADD DOCTOR
    // =====================================================================

    public void addDoctor() {
        System.out.println("\n--- Add New Doctor ---");
        System.out.print("Name              : "); String name           = scanner.nextLine().trim();
        System.out.print("Specialization    : "); String specialization = scanner.nextLine().trim();
        System.out.print("Phone             : "); String phone          = scanner.nextLine().trim();
        System.out.print("Email             : "); String email          = scanner.nextLine().trim();
        System.out.print("Experience (years): "); int    experience     = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Available Days (e.g. Mon,Wed,Fri): "); String days = scanner.nextLine().trim();

        String sql = "INSERT INTO doctors (name, specialization, phone, email, experience_years, available_days) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, specialization);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt   (5, experience);
            ps.setString(6, days);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Doctor added successfully!" : "\n[FAIL] Could not add doctor.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // VIEW ALL DOCTORS
    // =====================================================================

    public void viewDoctors() {
        System.out.println("\n--- Doctor List ---");
        String sql = "SELECT * FROM doctors ORDER BY id";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            printTableHeader();
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                printDoctorRow(rs);
            }
            if (!hasRows) System.out.println("| No doctors found.                                                                          |");
            System.out.println(BORDER);
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // UPDATE DOCTOR
    // =====================================================================

    public void updateDoctor() {
        System.out.println("\n--- Update Doctor ---");
        System.out.print("Enter Doctor ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (!doctorExists(id)) {
            System.out.println("[WARN] Doctor with ID " + id + " not found.");
            return;
        }

        System.out.print("New Name              : "); String name           = scanner.nextLine().trim();
        System.out.print("New Specialization    : "); String specialization = scanner.nextLine().trim();
        System.out.print("New Phone             : "); String phone          = scanner.nextLine().trim();
        System.out.print("New Email             : "); String email          = scanner.nextLine().trim();
        System.out.print("New Experience (years): "); int    experience     = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("New Available Days    : "); String days           = scanner.nextLine().trim();

        String sql = "UPDATE doctors SET name=?, specialization=?, phone=?, email=?, experience_years=?, available_days=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, specialization);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setInt   (5, experience);
            ps.setString(6, days);
            ps.setInt   (7, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Doctor updated." : "\n[FAIL] Update failed.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // DELETE DOCTOR
    // =====================================================================

    public void deleteDoctor() {
        System.out.println("\n--- Delete Doctor ---");
        System.out.print("Enter Doctor ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (!doctorExists(id)) {
            System.out.println("[WARN] Doctor with ID " + id + " not found.");
            return;
        }

        System.out.print("Confirm delete Doctor ID " + id + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Delete cancelled.");
            return;
        }

        String sql = "DELETE FROM doctors WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Doctor deleted." : "\n[FAIL] Could not delete doctor.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // SEARCH DOCTOR
    // =====================================================================

    public void searchDoctor() {
        System.out.println("\n--- Search Doctor ---");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.println("3. Search by Specialization");
        System.out.print("Choice: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());

        String sql;
        PreparedStatement ps;

        try {
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Doctor ID: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    sql = "SELECT * FROM doctors WHERE id=?";
                    ps  = connection.prepareStatement(sql);
                    ps.setInt(1, id);
                }
                case 2 -> {
                    System.out.print("Enter Name: ");
                    String name = "%" + scanner.nextLine().trim() + "%";
                    sql = "SELECT * FROM doctors WHERE name LIKE ?";
                    ps  = connection.prepareStatement(sql);
                    ps.setString(1, name);
                }
                default -> {
                    System.out.print("Enter Specialization: ");
                    String spec = "%" + scanner.nextLine().trim() + "%";
                    sql = "SELECT * FROM doctors WHERE specialization LIKE ?";
                    ps  = connection.prepareStatement(sql);
                    ps.setString(1, spec);
                }
            }

            ResultSet rs = ps.executeQuery();
            printTableHeader();
            boolean found = false;
            while (rs.next()) {
                found = true;
                printDoctorRow(rs);
            }
            if (!found) System.out.println("| No matching doctors found.                                                                 |");
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
     * Checks whether a doctor with the given ID exists.
     */
    public boolean doctorExists(int id) {
        String sql = "SELECT id FROM doctors WHERE id=?";
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

    private void printDoctorRow(ResultSet rs) throws SQLException {
        System.out.printf("| %-3d | %-20s | %-21s | %-3d | %-12s | %-23s |%n",
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("specialization"),
            rs.getInt("experience_years"),
            rs.getString("phone"),
            rs.getString("available_days"));
    }
}