package com.uithealthcare.domain.location;

import java.util.List;

public class DistrictResponse {

    boolean success;
    String message;
    private List<District> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<District> getData() {
        return data;
    }
}
