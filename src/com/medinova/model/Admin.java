package com.medinova.model;

/**
 * Admin - Model class representing an admin user.
 */
public class Admin {

    private int    id;
    private String username;
    private String password;

    // ---------- Constructors ----------

    public Admin() {}

    public Admin(int id, String username, String password) {
        this.id       = id;
        this.username = username;
        this.password = password;
    }

    // ---------- Getters & Setters ----------

    public int    getId()         { return id; }
    public void   setId(int id)   { this.id = id; }

    public String getUsername()                { return username; }
    public void   setUsername(String username) { this.username = username; }

    public String getPassword()                { return password; }
    public void   setPassword(String password) { this.password = password; }
}