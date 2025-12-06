package com.example.records.model;

public class ItemRecord {

    private String name;
    private String genId;
    private String careId;
    private String phone;
    private String relation;
    private String dob;
    private String gender;
    private String province;
    private String district;
    private String ward;
    private String addressDetail;

    // Full constructor
    public ItemRecord(String careId, String name, String genId, String phone, String relation,
                      String dob, String gender,
                      String province, String district, String ward, String addressDetail) {
        this.careId = careId;
        this.name = name;
        this.genId = genId;
        this.phone = phone;
        this.relation = relation;
        this.dob = dob;
        this.gender = gender;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.addressDetail = addressDetail;
    }

    // Constructor rút gọn (nếu bạn vẫn muốn dùng)
    public ItemRecord(String name, String id, String phone, String relation) {
        this.name = name;
        this.careId = id;
        this.phone = phone;
        this.relation = relation;
    }

    // Getters & Setters
    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCareProfileId() {
        return careId;
    }
    public String getGenId() {
        return genId;
    }

    public void setCareProfileId(String id) {
        this.careId = id;
    }
    public void setGenId(String id) {
        this.genId = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
}
