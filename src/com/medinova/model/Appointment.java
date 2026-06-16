package com.medinova.model;

/**
 * Appointment - Model class representing a patient-doctor appointment.
 */
public class Appointment {

    private int    id;
    private int    patientId;
    private int    doctorId;
    private String appointmentDate; // YYYY-MM-DD
    private String appointmentTime; // HH:MM
    private String status;          // SCHEDULED | CANCELLED | COMPLETED
    private String notes;

    // ---------- Constructors ----------

    public Appointment() {}

    public Appointment(int id, int patientId, int doctorId,
                       String appointmentDate, String appointmentTime,
                       String status, String notes) {
        this.id              = id;
        this.patientId       = patientId;
        this.doctorId        = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status          = status;
        this.notes           = notes;
    }

    // ---------- Getters & Setters ----------

    public int    getId()         { return id; }
    public void   setId(int id)   { this.id = id; }

    public int  getPatientId()           { return patientId; }
    public void setPatientId(int pid)    { this.patientId = pid; }

    public int  getDoctorId()            { return doctorId; }
    public void setDoctorId(int did)     { this.doctorId = did; }

    public String getAppointmentDate()               { return appointmentDate; }
    public void   setAppointmentDate(String date)    { this.appointmentDate = date; }

    public String getAppointmentTime()               { return appointmentTime; }
    public void   setAppointmentTime(String time)    { this.appointmentTime = time; }

    public String getStatus()              { return status; }
    public void   setStatus(String status) { this.status = status; }

    public String getNotes()             { return notes; }
    public void   setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format(
            "Appointment{id=%d, patientId=%d, doctorId=%d, date='%s', time='%s', status='%s'}",
            id, patientId, doctorId, appointmentDate, appointmentTime, status);
    }
}