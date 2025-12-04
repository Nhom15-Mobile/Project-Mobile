package com.uithealthcare.domain.location;

public class District {
    private String code;
    private String name;

    private String province_code;

    public String getProvince_code() {
        return province_code;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return name;
    }
}
