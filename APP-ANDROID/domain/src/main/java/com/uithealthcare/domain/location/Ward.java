package com.uithealthcare.domain.location;

public class Ward {
    private String code;
    private String name;
    private String district_code;

    public String getCode() {
        return code;
    }

    public String getDistrict_code() {
        return district_code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
