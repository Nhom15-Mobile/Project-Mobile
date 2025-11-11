package com.uithealthcare.domain.doctor;

import java.util.List;

import javax.print.Doc;

public class DoctorRespone {
    private boolean success;
    private String message;
    private List<Doctor> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<Doctor>  getData() { return data; }
}
