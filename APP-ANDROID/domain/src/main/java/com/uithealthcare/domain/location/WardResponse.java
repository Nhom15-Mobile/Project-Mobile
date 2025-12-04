package com.uithealthcare.domain.location;

import java.util.List;

public class WardResponse {
    private boolean success;
    private String message;
    private List<Ward> data;

    public List<Ward> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
