package com.uithealthcare.domain.careProfile;

public class CreateCareProfileResponse {
    private boolean success;
    private String message;

    public CreateCareProfileResponse() {
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
}
