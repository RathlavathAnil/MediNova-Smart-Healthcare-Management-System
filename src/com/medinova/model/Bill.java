package com.medinova.model;

/**
 * Bill - Model class representing a patient billing record.
 */
public class Bill {

    private int    id;
    private int    patientId;
    private int    appointmentId;
    private double consultationFee;
    private double medicineCost;
    private double testCost;
    private double totalAmount;
    private String paymentStatus; // PAID | PENDING | PARTIAL
    private String billDate;      // YYYY-MM-DD

    // ---------- Constructors ----------

    public Bill() {}

    public Bill(int id, int patientId, int appointmentId,
                double consultationFee, double medicineCost, double testCost,
                String paymentStatus, String billDate) {
        this.id              = id;
        this.patientId       = patientId;
        this.appointmentId   = appointmentId;
        this.consultationFee = consultationFee;
        this.medicineCost    = medicineCost;
        this.testCost        = testCost;
        this.totalAmount     = consultationFee + medicineCost + testCost;
        this.paymentStatus   = paymentStatus;
        this.billDate        = billDate;
    }

    // ---------- Getters & Setters ----------

    public int  getId()       { return id; }
    public void setId(int id) { this.id = id; }

    public int  getPatientId()        { return patientId; }
    public void setPatientId(int pid) { this.patientId = pid; }

    public int  getAppointmentId()         { return appointmentId; }
    public void setAppointmentId(int aid)  { this.appointmentId = aid; }

    public double getConsultationFee()              { return consultationFee; }
    public void   setConsultationFee(double fee)    { this.consultationFee = fee; recalcTotal(); }

    public double getMedicineCost()              { return medicineCost; }
    public void   setMedicineCost(double cost)   { this.medicineCost = cost; recalcTotal(); }

    public double getTestCost()              { return testCost; }
    public void   setTestCost(double cost)   { this.testCost = cost; recalcTotal(); }

    public double getTotalAmount()                { return totalAmount; }
    public void   setTotalAmount(double total)    { this.totalAmount = total; }

    public String getPaymentStatus()               { return paymentStatus; }
    public void   setPaymentStatus(String status)  { this.paymentStatus = status; }

    public String getBillDate()            { return billDate; }
    public void   setBillDate(String date) { this.billDate = date; }

    /** Recalculates total whenever a cost field changes. */
    private void recalcTotal() {
        this.totalAmount = this.consultationFee + this.medicineCost + this.testCost;
    }

    @Override
    public String toString() {
        return String.format(
            "Bill{id=%d, patientId=%d, total=%.2f, status='%s', date='%s'}",
            id, patientId, totalAmount, paymentStatus, billDate);
    }
}