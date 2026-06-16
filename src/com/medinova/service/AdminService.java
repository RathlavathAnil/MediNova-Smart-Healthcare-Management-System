package com.medinova.service;

import com.medinova.database.DatabaseConnection;

import java.sql.*;
import java.util.Scanner;

/**
 * AdminService - Handles admin authentication.
 */
public class AdminService {

    private final Connection connection;
    private final Scanner    scanner;

    public AdminService(Scanner scanner) {
        this.connection = DatabaseConnection.getConnection();
        this.scanner    = scanner;
    }

    /**
     * Prompts the user for credentials and validates against the admins table.
     * Returns true on successful login.
     */
    public boolean login() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║     MEDINOVA - Admin Login           ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.print("Username : ");
        String username = scanner.nextLine().trim();
        System.out.print("Password : ");
        String password = scanner.nextLine().trim();

        String sql = "SELECT id FROM admins WHERE username=? AND password=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("\n[SUCCESS] Login successful! Welcome, " + username + ".");
                return true;
            } else {
                System.out.println("\n[FAIL] Invalid username or password.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] " + e.getMessage());
            return false;
        }
    }
}