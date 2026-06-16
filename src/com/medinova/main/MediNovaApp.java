package com.medinova.main;

import com.medinova.database.DatabaseConnection;
import com.medinova.service.*;

import java.util.Scanner;

/**
 * MediNovaApp - Main entry point for the MediNova Smart Healthcare Management System.
 *
 * Launches admin login, then presents a hierarchical menu covering:
 *   Patient Management | Doctor Management | Appointments |
 *   Medical Records | Billing | Reports
 */
public class MediNovaApp {

    // ── service references ──
    private static AdminService         adminService;
    private static PatientService       patientService;
    private static DoctorService        doctorService;
    private static AppointmentService   appointmentService;
    private static MedicalRecordService medicalRecordService;
    private static BillingService       billingService;
    private static ReportService        reportService;

    private static final Scanner scanner = new Scanner(System.in);

    // ======================================================================
    // MAIN
    // ======================================================================

    public static void main(String[] args) {

        // Boot banner
        printBanner();

        // Initialise all services (each grabs the singleton connection internally)
        adminService         = new AdminService(scanner);
        patientService       = new PatientService(scanner);
        doctorService        = new DoctorService(scanner);
        appointmentService   = new AppointmentService(scanner, patientService, doctorService);
        medicalRecordService = new MedicalRecordService(scanner, patientService, doctorService);
        billingService       = new BillingService(scanner, patientService);
        reportService        = new ReportService(scanner);

        // Admin must log in (max 3 attempts)
        boolean loggedIn = false;
        for (int attempt = 1; attempt <= 3; attempt++) {
            if (adminService.login()) { loggedIn = true; break; }
            System.out.println("Attempt " + attempt + " of 3 failed.");
        }
        if (!loggedIn) {
            System.out.println("\nToo many failed login attempts. Exiting.");
            DatabaseConnection.closeConnection();
            return;
        }

        // Main application loop
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt();
            switch (choice) {
                case 1  -> patientMenu();
                case 2  -> doctorMenu();
                case 3  -> appointmentMenu();
                case 4  -> medicalRecordMenu();
                case 5  -> billingMenu();
                case 6  -> reportMenu();
                case 0  -> running = false;
                default -> System.out.println("[WARN] Invalid choice. Please try again.");
            }
        }

        System.out.println("\nThank you for using MediNova. Goodbye!");
        DatabaseConnection.closeConnection();
    }

    // ======================================================================
    // SUB-MENUS
    // ======================================================================

    // ── Patient Menu ──────────────────────────────────────────────────────

    private static void patientMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌─── Patient Management  ───────────────┐");
            System.out.println("│  1. Add Patient                       │");
            System.out.println("│  2. View All Patients                 │");
            System.out.println("│  3. Update Patient                    │");
            System.out.println("│  4. Delete Patient                    │");
            System.out.println("│  5. Search Patient                    │");
            System.out.println("│  0. Back to Main Menu                 │");
            System.out.println("└───────────────────────────────────────┘");
            System.out.print("Choice: ");
            switch (readInt()) {
                case 1  -> patientService.addPatient();
                case 2  -> patientService.viewPatients();
                case 3  -> patientService.updatePatient();
                case 4  -> patientService.deletePatient();
                case 5  -> patientService.searchPatient();
                case 0  -> back = true;
                default -> System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    // ── Doctor Menu ───────────────────────────────────────────────────────

    private static void doctorMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌─── Doctor Management  ────────────────┐");
            System.out.println("│  1. Add Doctor                        │");
            System.out.println("│  2. View All Doctors                  │");
            System.out.println("│  3. Update Doctor                     │");
            System.out.println("│  4. Delete Doctor                     │");
            System.out.println("│  5. Search Doctor                     │");
            System.out.println("│  0. Back to Main Menu                 │");
            System.out.println("└───────────────────────────────────────┘");
            System.out.print("Choice: ");
            switch (readInt()) {
                case 1  -> doctorService.addDoctor();
                case 2  -> doctorService.viewDoctors();
                case 3  -> doctorService.updateDoctor();
                case 4  -> doctorService.deleteDoctor();
                case 5  -> doctorService.searchDoctor();
                case 0  -> back = true;
                default -> System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    // ── Appointment Menu ──────────────────────────────────────────────────

    private static void appointmentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌─── Appointment Management  ────────────┐");
            System.out.println("│  1. Book Appointment                   │");
            System.out.println("│  2. View All Appointments              │");
            System.out.println("│  3. Cancel Appointment                 │");
            System.out.println("│  4. Reschedule Appointment             │");
            System.out.println("│  5. Check Doctor Availability          │");
            System.out.println("│  0. Back to Main Menu                  │");
            System.out.println("└────────────────────────────────────────┘");
            System.out.print("Choice: ");
            switch (readInt()) {
                case 1  -> appointmentService.bookAppointment();
                case 2  -> appointmentService.viewAppointments();
                case 3  -> appointmentService.cancelAppointment();
                case 4  -> appointmentService.rescheduleAppointment();
                case 5  -> appointmentService.checkAvailabilityMenu();
                case 0  -> back = true;
                default -> System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    // ── Medical Record Menu ───────────────────────────────────────────────

    private static void medicalRecordMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌───  Medical Records ────────────────────┐");
            System.out.println("│  1. Add Medical Record                  │");
            System.out.println("│  2. View All Records                    │");
            System.out.println("│  3. View Records by Patient             │");
            System.out.println("│  4. Update Medical Record               │");
            System.out.println("│  0. Back to Main Menu                   │");
            System.out.println("└─────────────────────────────────────────┘");
            System.out.print("Choice: ");
            switch (readInt()) {
                case 1  -> medicalRecordService.addMedicalRecord();
                case 2  -> medicalRecordService.viewAllMedicalRecords();
                case 3  -> medicalRecordService.viewRecordsByPatient();
                case 4  -> medicalRecordService.updateMedicalRecord();
                case 0  -> back = true;
                default -> System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    // ── Billing Menu ──────────────────────────────────────────────────────

    private static void billingMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌─── Billing System  ─────────────────────┐");
            System.out.println("│  1. Generate Bill                       │");
            System.out.println("│  2. View All Bills                      │");
            System.out.println("│  3. View Bills by Patient               │");
            System.out.println("│  4. Update Payment Status               │");
            System.out.println("│  0. Back to Main Menu                   │");
            System.out.println("└─────────────────────────────────────────┘");
            System.out.print("Choice: ");
            switch (readInt()) {
                case 1  -> billingService.generateBill();
                case 2  -> billingService.viewBills();
                case 3  -> billingService.viewBillByPatient();
                case 4  -> billingService.updatePaymentStatus();
                case 0  -> back = true;
                default -> System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    // ── Report Menu ───────────────────────────────────────────────────────

    private static void reportMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n┌───  Reports  ───────────────────────────┐");
            System.out.println("│  1. Summary Report                      │");
            System.out.println("│  2. Total Patients Report               │");
            System.out.println("│  3. Total Doctors Report                │");
            System.out.println("│  4. Total Appointments Report           │");
            System.out.println("│  5. Daily Appointment Report            │");
            System.out.println("│  0. Back to Main Menu                   │");
            System.out.println("└─────────────────────────────────────────┘");
            System.out.print("Choice: ");
            switch (readInt()) {
                case 1  -> reportService.printSummaryReport();
                case 2  -> reportService.totalPatientsReport();
                case 3  -> reportService.totalDoctorsReport();
                case 4  -> reportService.totalAppointmentsReport();
                case 5  -> reportService.dailyAppointmentReport();
                case 0  -> back = true;
                default -> System.out.println("[WARN] Invalid choice.");
            }
        }
    }

    // ======================================================================
    // DISPLAY HELPERS
    // ======================================================================

    private static void printBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║                                                  ║");
        System.out.println("║    ███╗   ███╗███████╗██████╗ ██╗                ║");
        System.out.println("║    ████╗ ████║██╔════╝██╔══██╗██║                ║");
        System.out.println("║    ██╔████╔██║█████╗  ██║  ██║██║                ║");
        System.out.println("║    ██║╚██╔╝██║██╔══╝  ██║  ██║██║                ║");
        System.out.println("║    ██║ ╚═╝ ██║███████╗██████╔╝██║                ║");
        System.out.println("║    ╚═╝     ╚═╝╚══════╝╚═════╝ ╚═╝                ║");
        System.out.println("║                                                  ║");
        System.out.println("║    MediNova - Smart Healthcare Management        ║");
        System.out.println("║    Powered by Java 21 + JDBC + MySQL             ║");
        System.out.println("║                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMainMenu() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║           MAIN MENU                      ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Patient Management                   ║");
        System.out.println("║  2. Doctor Management                    ║");
        System.out.println("║  3. Appointment Management               ║");
        System.out.println("║  4. Medical Records                      ║");
        System.out.println("║  5. Billing System                       ║");
        System.out.println("║  6. Reports                              ║");
        System.out.println("║  0. Exit                                 ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.print("Enter your choice: ");
    }

    /**
     * Safe integer reader; returns -1 on invalid input instead of throwing.
     */
    private static int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}