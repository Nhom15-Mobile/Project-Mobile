package com.example.results.model;

public class ResultItem {
    private String id;
    private String service;
    private String examResult;
    private String examDate;
    private String status;
    private Doctor doctor;

    public static class Doctor {
        private String fullName;

        public String getFullName() {
            return fullName;
        }
    }

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

    public Doctor getDoctor() {
        return doctor;
    }
}
