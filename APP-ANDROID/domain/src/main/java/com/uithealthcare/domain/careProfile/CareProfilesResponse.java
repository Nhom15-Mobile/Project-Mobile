package com.uithealthcare.domain.careProfile;

import java.util.List;

public class CareProfilesResponse {
    private boolean success;
    private String message;
    private List<CareProfile> data;

    public CareProfilesResponse() {
    };

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

    public List<CareProfile> getData() {
        return data;
    }

    public void setData(List<CareProfile> data) {
        this.data = data;
    }
}
