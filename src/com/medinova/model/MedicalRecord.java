package com.medinova.model;

/**
 * MedicalRecord - Model class representing a patient's medical record.
 */
public class MedicalRecord {

    private int    id;
    private int    patientId;
    private int    doctorId;
    private String diagnosis;
    private String prescription;
    private String recordDate;   // YYYY-MM-DD
    private String notes;

    // ---------- Constructors ----------

    public MedicalRecord() {}

    public MedicalRecord(int id, int patientId, int doctorId,
                         String diagnosis, String prescription,
                         String recordDate, String notes) {
        this.id           = id;
        this.patientId    = patientId;
        this.doctorId     = doctorId;
        this.diagnosis    = diagnosis;
        this.prescription = prescription;
        this.recordDate   = recordDate;
        this.notes        = notes;
    }

    // ---------- Getters & Setters ----------

    public int  getId()       { return id; }
    public void setId(int id) { this.id = id; }

    public int  getPatientId()        { return patientId; }
    public void setPatientId(int pid) { this.patientId = pid; }

    public int  getDoctorId()         { return doctorId; }
    public void setDoctorId(int did)  { this.doctorId = did; }

    public String getDiagnosis()               { return diagnosis; }
    public void   setDiagnosis(String d)       { this.diagnosis = d; }

    public String getPrescription()              { return prescription; }
    public void   setPrescription(String p)      { this.prescription = p; }

    public String getRecordDate()              { return recordDate; }
    public void   setRecordDate(String date)   { this.recordDate = date; }

    public String getNotes()             { return notes; }
    public void   setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format(
            "MedicalRecord{id=%d, patientId=%d, doctorId=%d, date='%s', diagnosis='%s'}",
            id, patientId, doctorId, recordDate, diagnosis);
    }
}