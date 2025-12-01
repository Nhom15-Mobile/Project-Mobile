package com.uithealthcare.domain.payment;

public class PaymentResponse {
    private boolean success;
    private String message;
    private Payment data;

    public PaymentResponse() {
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

    public Payment getData() {
        return data;
    }

    public void setData(Payment data) {
        this.data = data;
    }
}
