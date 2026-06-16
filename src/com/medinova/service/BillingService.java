package com.medinova.service;

import com.medinova.database.DatabaseConnection;

import java.sql.*;
import java.util.Scanner;

/**
 * BillingService - Handles bill generation, viewing, and payment status updates.
 */
public class BillingService {

    private final Connection     connection;
    private final Scanner        scanner;
    private final PatientService patientService;

    public BillingService(Scanner scanner, PatientService patientService) {
        this.connection     = DatabaseConnection.getConnection();
        this.scanner        = scanner;
        this.patientService = patientService;
    }

    // =====================================================================
    // GENERATE BILL
    // =====================================================================

    public void generateBill() {
        System.out.println("\n--- Generate Bill ---");
        System.out.print("Patient ID        : "); int    patientId     = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Appointment ID    : "); int    appointmentId = Integer.parseInt(scanner.nextLine().trim());

        if (!patientService.patientExists(patientId)) {
            System.out.println("[WARN] Patient ID " + patientId + " not found.");
            return;
        }

        System.out.print("Consultation Fee  : "); double consultFee   = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Medicine Cost     : "); double medicineCost = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Test/Lab Cost     : "); double testCost     = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Bill Date (YYYY-MM-DD): "); String billDate = scanner.nextLine().trim();

        double total = consultFee + medicineCost + testCost;

        String sql = "INSERT INTO bills (patient_id, appointment_id, consultation_fee, medicine_cost, test_cost, total_amount, payment_status, bill_date) VALUES (?,?,?,?,?,?,'PENDING',?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, patientId);
            ps.setInt   (2, appointmentId);
            ps.setDouble(3, consultFee);
            ps.setDouble(4, medicineCost);
            ps.setDouble(5, testCost);
            ps.setDouble(6, total);
            ps.setString(7, billDate);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("\n========================================");
                System.out.println("         BILL GENERATED SUCCESSFULLY    ");
                System.out.println("========================================");
                System.out.printf ("  Patient ID      : %d%n",    patientId);
                System.out.printf ("  Consultation Fee: Rs %.2f%n", consultFee);
                System.out.printf ("  Medicine Cost   : Rs %.2f%n", medicineCost);
                System.out.printf ("  Test/Lab Cost   : Rs %.2f%n", testCost);
                System.out.println("  ----------------------------------------");
                System.out.printf ("  TOTAL AMOUNT    : Rs %.2f%n", total);
                System.out.println("  Payment Status  : PENDING");
                System.out.println("========================================");
            } else {
                System.out.println("[FAIL] Could not generate bill.");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // VIEW ALL BILLS
    // =====================================================================

    public void viewBills() {
        System.out.println("\n--- All Bills ---");
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC";
        String border = "+-----+------------+----------+----------+---------+---------+----------+---------+------------+";
        String header = "| ID  | Patient ID | Appt ID  | Consult  | Meds    | Tests   | Total    | Status  | Date       |";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println(border);
            System.out.println(header);
            System.out.println(border);
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                System.out.printf("| %-3d | %-10d | %-8d | %-8.2f | %-7.2f | %-7.2f | %-8.2f | %-7s | %-10s |%n",
                    rs.getInt("id"),
                    rs.getInt("patient_id"),
                    rs.getInt("appointment_id"),
                    rs.getDouble("consultation_fee"),
                    rs.getDouble("medicine_cost"),
                    rs.getDouble("test_cost"),
                    rs.getDouble("total_amount"),
                    rs.getString("payment_status"),
                    rs.getString("bill_date"));
            }
            if (!hasRows) System.out.println("| No bills found.                                                                                             |");
            System.out.println(border);
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // VIEW BILL BY PATIENT
    // =====================================================================

    public void viewBillByPatient() {
        System.out.println("\n--- View Bills by Patient ---");
        System.out.print("Enter Patient ID: ");
        int patientId = Integer.parseInt(scanner.nextLine().trim());

        String sql = "SELECT * FROM bills WHERE patient_id=? ORDER BY bill_date DESC";
        String border = "+-----+----------+---------+---------+----------+---------+------------+";
        String header = "| ID  | Consult  | Meds    | Tests   | Total    | Status  | Date       |";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\nBills for Patient ID: " + patientId);
            System.out.println(border);
            System.out.println(header);
            System.out.println(border);
            boolean found   = false;
            double  grandTotal = 0;
            while (rs.next()) {
                found = true;
                double total = rs.getDouble("total_amount");
                grandTotal += total;
                System.out.printf("| %-3d | %-8.2f | %-7.2f | %-7.2f | %-8.2f | %-7s | %-10s |%n",
                    rs.getInt("id"),
                    rs.getDouble("consultation_fee"),
                    rs.getDouble("medicine_cost"),
                    rs.getDouble("test_cost"),
                    total,
                    rs.getString("payment_status"),
                    rs.getString("bill_date"));
            }
            if (!found) {
                System.out.println("| No bills found for this patient.                              |");
            } else {
                System.out.println(border);
                System.out.printf("  GRAND TOTAL: Rs %.2f%n", grandTotal);
            }
            System.out.println(border);
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // UPDATE PAYMENT STATUS
    // =====================================================================

    public void updatePaymentStatus() {
        System.out.println("\n--- Update Payment Status ---");
        System.out.print("Enter Bill ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Payment Status:");
        System.out.println("  1. PAID");
        System.out.println("  2. PENDING");
        System.out.println("  3. PARTIAL");
        System.out.print("Choice: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());

        String status = switch (choice) {
            case 1  -> "PAID";
            case 3  -> "PARTIAL";
            default -> "PENDING";
        };

        String sql = "UPDATE bills SET payment_status=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt   (2, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                ? "\n[SUCCESS] Payment status updated to " + status + "."
                : "\n[FAIL] Bill ID " + id + " not found.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }
}