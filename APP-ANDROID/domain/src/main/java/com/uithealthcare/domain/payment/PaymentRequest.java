package com.uithealthcare.domain.payment;

public class PaymentRequest {
    private String appointmentId;

    public PaymentRequest(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
