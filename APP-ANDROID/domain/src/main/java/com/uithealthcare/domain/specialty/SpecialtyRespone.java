package com.uithealthcare.domain.specialty;

import java.util.List;

public class SpecialtyRespone {
    private boolean success;
    private String message;

    List<Specialty> data;

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

    public List<Specialty> getData() {
        return data;
    }

    public void setData(List<Specialty> data) {
        this.data = data;
    }

    public SpecialtyRespone() {
    }
}
