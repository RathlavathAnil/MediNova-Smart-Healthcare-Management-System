package com.medinova.service;

import com.medinova.database.DatabaseConnection;

import java.sql.*;
import java.util.Scanner;

/**
 * AppointmentService - Handles appointment booking, viewing, cancellation,
 * rescheduling, and doctor availability checking.
 */
public class AppointmentService {

    private final Connection     connection;
    private final Scanner        scanner;
    private final PatientService patientService;
    private final DoctorService  doctorService;

    private static final String BORDER =
        "+-----+------------+------------+------------+-------+------------+-------------------------------------+";
    private static final String HEADER =
        "| ID  | Patient ID | Doctor ID  | Date       | Time  | Status     | Notes                               |";

    public AppointmentService(Scanner scanner,
                              PatientService patientService,
                              DoctorService  doctorService) {
        this.connection     = DatabaseConnection.getConnection();
        this.scanner        = scanner;
        this.patientService = patientService;
        this.doctorService  = doctorService;
    }

    // =====================================================================
    // BOOK APPOINTMENT
    // =====================================================================

    public void bookAppointment() {
        System.out.println("\n--- Book Appointment ---");
        System.out.print("Patient ID        : ");
        int patientId = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Doctor ID         : ");
        int doctorId = Integer.parseInt(scanner.nextLine().trim());

        if (!patientService.patientExists(patientId)) {
            System.out.println("[WARN] Patient ID " + patientId + " not found.");
            return;
        }
        if (!doctorService.doctorExists(doctorId)) {
            System.out.println("[WARN] Doctor ID " + doctorId + " not found.");
            return;
        }

        System.out.print("Appointment Date (YYYY-MM-DD): "); String date  = scanner.nextLine().trim();
        System.out.print("Appointment Time (HH:MM)     : "); String time  = scanner.nextLine().trim();
        System.out.print("Notes (optional)             : "); String notes = scanner.nextLine().trim();

        if (!checkDoctorAvailability(doctorId, date, time)) {
            System.out.println("[WARN] Doctor is already booked on " + date + " at " + time + ".");
            return;
        }

        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status, notes) VALUES (?,?,?,?,'SCHEDULED',?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, patientId);
            ps.setInt   (2, doctorId);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.setString(5, notes);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Appointment booked!" : "\n[FAIL] Could not book appointment.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // VIEW APPOINTMENTS
    // =====================================================================

    public void viewAppointments() {
        System.out.println("\n--- All Appointments ---");
        String sql = "SELECT * FROM appointments ORDER BY appointment_date, appointment_time";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            printTableHeader();
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                printAppointmentRow(rs);
            }
            if (!hasRows) System.out.println("| No appointments found.                                                                                           |");
            System.out.println(BORDER);
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // CANCEL APPOINTMENT
    // =====================================================================

    public void cancelAppointment() {
        System.out.println("\n--- Cancel Appointment ---");
        System.out.print("Enter Appointment ID to cancel: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (!appointmentExists(id)) {
            System.out.println("[WARN] Appointment ID " + id + " not found.");
            return;
        }

        String sql = "UPDATE appointments SET status='CANCELLED' WHERE id=? AND status='SCHEDULED'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Appointment cancelled." : "\n[FAIL] Could not cancel (may already be cancelled/completed).");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // RESCHEDULE APPOINTMENT
    // =====================================================================

    public void rescheduleAppointment() {
        System.out.println("\n--- Reschedule Appointment ---");
        System.out.print("Enter Appointment ID to reschedule: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (!appointmentExists(id)) {
            System.out.println("[WARN] Appointment ID " + id + " not found.");
            return;
        }

        // Get the doctor ID for this appointment to re-check availability
        int doctorId = getDoctorIdForAppointment(id);

        System.out.print("New Date (YYYY-MM-DD): "); String newDate = scanner.nextLine().trim();
        System.out.print("New Time (HH:MM)     : "); String newTime = scanner.nextLine().trim();

        if (!checkDoctorAvailability(doctorId, newDate, newTime)) {
            System.out.println("[WARN] Doctor is already booked on " + newDate + " at " + newTime + ".");
            return;
        }

        String sql = "UPDATE appointments SET appointment_date=?, appointment_time=?, status='SCHEDULED' WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newDate);
            ps.setString(2, newTime);
            ps.setInt   (3, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "\n[SUCCESS] Appointment rescheduled." : "\n[FAIL] Reschedule failed.");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

    // =====================================================================
    // CHECK DOCTOR AVAILABILITY
    // =====================================================================

    /**
     * Returns true if the doctor has no SCHEDULED appointment at the given date+time.
     */
    public boolean checkDoctorAvailability(int doctorId, String date, String time) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=? AND appointment_time=? AND status='SCHEDULED'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt   (1, doctorId);
            ps.setString(2, date);
            ps.setString(3, time);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return false;
    }

    /**
     * Interactive availability check callable from menu.
     */
    public void checkAvailabilityMenu() {
        System.out.println("\n--- Check Doctor Availability ---");
        System.out.print("Doctor ID        : "); int    doctorId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Date (YYYY-MM-DD): "); String date     = scanner.nextLine().trim();
        System.out.print("Time (HH:MM)     : "); String time     = scanner.nextLine().trim();

        boolean available = checkDoctorAvailability(doctorId, date, time);
        System.out.println(available
            ? "\n[INFO] Doctor is AVAILABLE on " + date + " at " + time
            : "\n[INFO] Doctor is NOT available on " + date + " at " + time);
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    public boolean appointmentExists(int id) {
        String sql = "SELECT id FROM appointments WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    private int getDoctorIdForAppointment(int appointmentId) {
        String sql = "SELECT doctor_id FROM appointments WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("doctor_id");
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
        return -1;
    }

    private void printTableHeader() {
        System.out.println(BORDER);
        System.out.println(HEADER);
        System.out.println(BORDER);
    }

    private void printAppointmentRow(ResultSet rs) throws SQLException {
        System.out.printf("| %-3d | %-10d | %-10d | %-10s | %-5s | %-10s | %-35s |%n",
            rs.getInt("id"),
            rs.getInt("patient_id"),
            rs.getInt("doctor_id"),
            rs.getString("appointment_date"),
            rs.getString("appointment_time"),
            rs.getString("status"),
            rs.getString("notes") != null ? rs.getString("notes") : "");
    }
}