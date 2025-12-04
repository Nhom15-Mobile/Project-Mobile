package com.uithealthcare.domain.location;

public class Province {
    private String code;
    private String name;

    public String getCode() { return code; }
    public String getName() { return name; }

    // Để AutoComplete hiển thị tên tỉnh thay vì object
    @Override
    public String toString() {
        return name;
    }
}
