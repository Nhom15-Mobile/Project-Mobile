package com.example.results.model;

public class ResultItem {
    private String id;
    private String service;
    private String examResult;
    private String scheduledAt;
    private String status;
    private Doctor doctor;

    private String recommendation;
    public String getRecommendation(){
        return recommendation;
    }
    public String getScheduledAt() { return scheduledAt; }

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


    public String getStatus() {
        return status;
    }

    public Doctor getDoctor() {
        return doctor;
    }
}
