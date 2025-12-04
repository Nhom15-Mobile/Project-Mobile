package com.uithealthcare.domain.careProfile;

public class CreateCareProfileRequest {

    private String fullName;
    private String relation;
    private String phone;
    private String country;
    private String gender;
    private String dob;           // yyyy-MM-dd
    private String provinceCode;
    private String districtCode;
    private String wardCode;
    private String addressDetail;

    public CreateCareProfileRequest(String fullName,
                              String relation,
                              String phone,
                              String country,
                              String gender,
                              String dob,
                              String provinceCode,
                              String districtCode,
                              String wardCode,
                              String addressDetail) {
        this.fullName = fullName;
        this.relation = relation;
        this.phone = phone;
        this.country = country;
        this.gender = gender;
        this.dob = dob;
        this.provinceCode = provinceCode;
        this.districtCode = districtCode;
        this.wardCode = wardCode;
        this.addressDetail = addressDetail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
}
