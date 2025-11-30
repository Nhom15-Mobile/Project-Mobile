package com.uithealthcare.domain.appointment;

import java.io.Serializable;

public class AppointmentInfo implements Serializable {

    public static final String EXTRA = "extra_appointment_info";
    private String id;
    private String patientName;
    private String specialty;
    private String examDate;
    private String examHour;
    private String clinic;
    private String price;
    private String createdDate;

    public AppointmentInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getExamHour() {
        return examHour;
    }

    public void setExamHour(String examHour) {
        this.examHour = examHour;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
