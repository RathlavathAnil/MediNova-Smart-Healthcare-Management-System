package com.medinova.service;

import com.medinova.database.DatabaseConnection;

import java.sql.*;
import java.util.Scanner;

/**
 * ReportService - Generates statistical and summary reports.
 */
public class ReportService {

    private final Connection connection;
    private final Scanner    scanner;

    public ReportService(Scanner scanner) {
        this.connection = DatabaseConnection.getConnection();
        this.scanner    = scanner;
    }

    // =====================================================================
    // SUMMARY REPORT
    // =====================================================================

    /**
     * Prints a full summary: total patients, doctors, appointments, revenue.
     */
    public void printSummaryReport() {
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║       MEDINOVA SUMMARY REPORT            ║");
        System.out.println("╚══════════════════════════════════════════╝");

        System.out.println("\n  PATIENTS");
        printCount("Total Patients",    "SELECT COUNT(*) FROM patients");

        System.out.println("\n  DOCTORS");
        printCount("Total Doctors",     "SELECT COUNT(*) FROM doctors");

        System.out.println("\n  APPOINTMENTS");
        printCount("Total Appointments","SELECT COUNT(*) FROM appointments");
        printCount("Scheduled",         "SELECT COUNT(*) FROM appointments WHERE status='SCHEDULED'");
        printCount("Completed",         "SELECT COUNT(*) FROM appointments WHERE status='COMPLETED'");
        printCount("Cancelled",         "SELECT COUNT(*) FROM appointments WHERE status='CANCELLED'");

        System.out.println("\n  BILLING");
        printDoubleCount("Total Revenue (Paid)", "SELECT COALESCE(SUM(total_amount),0) FROM bills WHERE payment_status='PAID'");
        printDoubleCount("Pending Amount",        "SELECT COALESCE(SUM(total_amount),0) FROM bills WHERE payment_status='PENDING'");

        System.out.println("\n  MEDICAL RECORDS");
        printCount("Total Records", "SELECT COUNT(*) FROM medical_records");
        System.out.println();
    }

    // =====================================================================
    // TOTAL PATIENTS REPORT
    // =====================================================================

    public void totalPatientsReport() {
        System.out.println("\n--- Total Patients by Gender ---");
        String sql = "SELECT gender, COUNT(*) AS cnt FROM patients GROUP BY gender";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("+--------+-------+");
            System.out.println("| Gender | Count |");
            System.out.println("+--------+-------+");
            while (rs.next()) {
                System.out.printf("| %-6s | %-5d |%n", rs.getString("gender"), rs.getInt("cnt"));
            }
            System.out.println("+--------+-------+");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // TOTAL DOCTORS REPORT
    // =====================================================================

    public void totalDoctorsReport() {
        System.out.println("\n--- Doctors by Specialization ---");
        String sql = "SELECT specialization, COUNT(*) AS cnt FROM doctors GROUP BY specialization ORDER BY cnt DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("+---------------------------+-------+");
            System.out.println("| Specialization            | Count |");
            System.out.println("+---------------------------+-------+");
            while (rs.next()) {
                System.out.printf("| %-25s | %-5d |%n",
                    rs.getString("specialization"), rs.getInt("cnt"));
            }
            System.out.println("+---------------------------+-------+");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // TOTAL APPOINTMENTS REPORT
    // =====================================================================

    public void totalAppointmentsReport() {
        System.out.println("\n--- Appointments by Status ---");
        String sql = "SELECT status, COUNT(*) AS cnt FROM appointments GROUP BY status";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("+-------------+-------+");
            System.out.println("| Status      | Count |");
            System.out.println("+-------------+-------+");
            while (rs.next()) {
                System.out.printf("| %-11s | %-5d |%n", rs.getString("status"), rs.getInt("cnt"));
            }
            System.out.println("+-------------+-------+");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // DAILY APPOINTMENT REPORT
    // =====================================================================

    public void dailyAppointmentReport() {
        System.out.println("\n--- Daily Appointment Report ---");
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();

        String sql = "SELECT a.id, p.name AS patient, d.name AS doctor, a.appointment_time, a.status, a.notes " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.id " +
                     "JOIN doctors  d ON a.doctor_id  = d.id " +
                     "WHERE a.appointment_date = ? " +
                     "ORDER BY a.appointment_time";

        String border = "+-----+----------------------+----------------------+-------+-------------+----------------------+";
        String header = "| ID  | Patient              | Doctor               | Time  | Status      | Notes                |";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            System.out.println("\nAppointments on " + date + ":");
            System.out.println(border);
            System.out.println(header);
            System.out.println(border);
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("| %-3d | %-20s | %-20s | %-5s | %-11s | %-20s |%n",
                    rs.getInt("id"),
                    rs.getString("patient"),
                    rs.getString("doctor"),
                    rs.getString("appointment_time"),
                    rs.getString("status"),
                    rs.getString("notes") != null ? rs.getString("notes") : "");
            }
            if (!found) System.out.println("| No appointments found on " + date + ".                                                         |");
            System.out.println(border);
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    private void printCount(String label, String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) System.out.printf("  %-30s : %d%n", label, rs.getInt(1));
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    private void printDoubleCount(String label, String sql) {
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) System.out.printf("  %-30s : Rs %.2f%n", label, rs.getDouble(1));
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }
}