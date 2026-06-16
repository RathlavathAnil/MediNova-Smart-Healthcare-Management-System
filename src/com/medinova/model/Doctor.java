package com.medinova.model;

/**
 * Doctor - Model class representing a doctor record.
 */
public class Doctor {

    private int    id;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private int    experienceYears;
    private String availableDays; // e.g. "Mon,Tue,Wed"

    // ---------- Constructors ----------

    public Doctor() {}

    public Doctor(int id, String name, String specialization, String phone,
                  String email, int experienceYears, String availableDays) {
        this.id              = id;
        this.name            = name;
        this.specialization  = specialization;
        this.phone           = phone;
        this.email           = email;
        this.experienceYears = experienceYears;
        this.availableDays   = availableDays;
    }

    // ---------- Getters & Setters ----------

    public int    getId()         { return id; }
    public void   setId(int id)   { this.id = id; }

    public String getName()            { return name; }
    public void   setName(String name) { this.name = name; }

    public String getSpecialization()                    { return specialization; }
    public void   setSpecialization(String s)            { this.specialization = s; }

    public String getPhone()             { return phone; }
    public void   setPhone(String phone) { this.phone = phone; }

    public String getEmail()             { return email; }
    public void   setEmail(String email) { this.email = email; }

    public int  getExperienceYears()             { return experienceYears; }
    public void setExperienceYears(int years)    { this.experienceYears = years; }

    public String getAvailableDays()               { return availableDays; }
    public void   setAvailableDays(String days)    { this.availableDays = days; }

    @Override
    public String toString() {
        return String.format("Doctor{id=%d, name='%s', specialization='%s', experience=%d yrs}",
                id, name, specialization, experienceYears);
    }
}