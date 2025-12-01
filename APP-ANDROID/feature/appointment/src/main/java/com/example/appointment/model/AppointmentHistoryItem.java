package com.example.appointment.model;

public class AppointmentHistoryItem {

    private String appointmentId;
    private String doctorName;
    private String specialtyName;
    private String date;          // "20/12/2025"
    private String time;          // "08:30"
    private String clinicName;
    private String status;        // UPCOMING / DONE / CANCELED
    private String paymentStatus; // "Đã thanh toán" / "Chưa thanh toán"

    // private String patientName;

    public AppointmentHistoryItem() {
    }

    public AppointmentHistoryItem(String appointmentId, String doctorName, String specialtyName,
                                  String date, String time, String clinicName,
                                  String status, String paymentStatus) {
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.specialtyName = specialtyName;
        this.date = date;
        this.time = time;
        this.clinicName = clinicName;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialtyName() { return specialtyName; }
    public void setSpecialtyName(String specialtyName) { this.specialtyName = specialtyName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getClinicName() { return clinicName; }
    public void setClinicName(String clinicName) { this.clinicName = clinicName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
