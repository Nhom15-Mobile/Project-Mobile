package com.uithealthcare.domain.location;

import java.util.List;

public class ProvinceResponse {
    boolean success;
    String message;
    private List<Province> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Province> getData() {
        return data;
    }
}