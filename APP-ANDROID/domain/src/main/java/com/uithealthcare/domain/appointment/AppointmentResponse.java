package com.uithealthcare.domain.appointment;

public class AppointmentResponse {
    private boolean success;
    private String message;
    private AppointmentData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public AppointmentData getData() {
        return data;
    }
}
