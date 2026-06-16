package com.medinova.model;

/**
 * Patient - Model class representing a patient record.
 */
public class Patient {

    private int    id;
    private String name;
    private int    age;
    private String gender;
    private String phone;
    private String address;
    private String bloodGroup;

    // ---------- Constructors ----------

    public Patient() {}

    public Patient(int id, String name, int age, String gender,
                   String phone, String address, String bloodGroup) {
        this.id         = id;
        this.name       = name;
        this.age        = age;
        this.gender     = gender;
        this.phone      = phone;
        this.address    = address;
        this.bloodGroup = bloodGroup;
    }

    // ---------- Getters & Setters ----------

    public int    getId()           { return id; }
    public void   setId(int id)     { this.id = id; }

    public String getName()              { return name; }
    public void   setName(String name)   { this.name = name; }

    public int    getAge()           { return age; }
    public void   setAge(int age)    { this.age = age; }

    public String getGender()              { return gender; }
    public void   setGender(String gender) { this.gender = gender; }

    public String getPhone()             { return phone; }
    public void   setPhone(String phone) { this.phone = phone; }

    public String getAddress()               { return address; }
    public void   setAddress(String address) { this.address = address; }

    public String getBloodGroup()                  { return bloodGroup; }
    public void   setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    @Override
    public String toString() {
        return String.format("Patient{id=%d, name='%s', age=%d, gender='%s', phone='%s', bloodGroup='%s'}",
                id, name, age, gender, phone, bloodGroup);
    }
}