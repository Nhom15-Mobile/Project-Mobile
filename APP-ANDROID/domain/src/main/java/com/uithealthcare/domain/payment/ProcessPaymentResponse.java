package com.uithealthcare.domain.payment;

import com.uithealthcare.domain.appointment.AppointmentData;

import java.util.List;

public class ProcessPaymentResponse {
    private boolean success;
    private String message;
    private List<AppointmentData> data;

    public ProcessPaymentResponse() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AppointmentData> getData() {
        return data;
    }

    public void setData(List<AppointmentData> data) {
        this.data = data;
    }
}
