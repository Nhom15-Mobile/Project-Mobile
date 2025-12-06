package com.uithealthcare.domain.result;

import java.util.List;

public class ResultResponse {
    private boolean success;
    private String message;
    private List<ResultData> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<ResultData> getData() {
        return data;
    }
}
