package com.uithealthcare.domain.appointment;


import com.uithealthcare.domain.careProfile.CareProfile;
import com.uithealthcare.domain.doctor.Doctor;
import com.uithealthcare.domain.doctor.Slot;
import com.uithealthcare.domain.patient.Patient;
import com.uithealthcare.domain.payment.Payment;

public class AppointmentData {
    private String id;
    private String patientId;
    private String doctorId;
    private String careProfileId;
    private String slotId;
    private String service;
    private String scheduledAt;
    private String status;
    private String paymentStatus;
    private String paymentId;
    private String qrCode;
    private String cancelReason;
    private String createdAt;
    private String updatedAt;

    private Patient patient;
    private Doctor doctor;
    private Slot slot;
    private CareProfile careProfile;
    private Payment payment; // bên domain

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public String getCareProfileId() { return careProfileId; }
    public String getSlotId() { return slotId; }
    public String getService() { return service; }
    public String getScheduledAt() { return scheduledAt; }
    public String getStatus() { return status; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentId() { return paymentId; }
    public String getQrCode() { return qrCode; }
    public String getCancelReason() { return cancelReason; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public Slot getSlot() { return slot; }
    public CareProfile getCareProfile() { return careProfile; }
    public Payment getPayment() { return payment; }
}

