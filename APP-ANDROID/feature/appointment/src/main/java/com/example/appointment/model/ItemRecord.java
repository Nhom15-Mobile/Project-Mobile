package com.example.appointment.model;

public class ItemRecord {
    private String name;
    private String id;
    private String phone;
    private String relationship;
    private String genid;

    public ItemRecord(String name, String id, String phone,String relationship,String genid) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.relationship = relationship;
        this.genid = genid;
    }

    public String getRelation(){return relationship;}
    public void setRelation(String relationship){
        this.relationship = relationship;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getGenId() {
        return genid;
    }

    public void setGenId(String id) {
        this.genid = genid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
