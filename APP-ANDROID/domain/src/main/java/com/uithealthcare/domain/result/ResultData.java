package com.uithealthcare.domain.result;

import com.uithealthcare.domain.careProfile.CareProfile;
import com.uithealthcare.domain.doctor.Doctor;

public class ResultData {

    private String id;
    private String service;
    private String examResult;
    private String examDate;
    private String status;
    private String paymentStatus;

    private Patient patient;
    private CareProfile careProfile;
    private Doctor doctor;

    // ====== NESTED MODELS ======
    public static class Patient {
        private String id;
        private String fullName;
        private String email;
        private String phone;

        public String getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }
    }


    // ====== GETTERS ======
    public String getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getExamResult() {
        return examResult;
    }

    public String getExamDate() {
        return examDate;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Patient getPatient() {
        return patient;
    }

    public CareProfile getCareProfile() {
        return careProfile;
    }

    public Doctor getDoctor() {
        return doctor;
    }
}
