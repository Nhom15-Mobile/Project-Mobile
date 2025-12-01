package com.uithealthcare.domain.appointment;

import java.util.List;

public class AppointmentHistoryResponse {
    private boolean success;
    private String message;
    private List<AppointmentData> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<AppointmentData> getData() {
        return data;
    }
}
