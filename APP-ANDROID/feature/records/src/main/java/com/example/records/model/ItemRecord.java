package com.example.records.model;

public class ItemRecord {
    private String name;
    private String id;
    private String phone;
    private String relationship;

    public ItemRecord(String name, String id, String phone,String relationship) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.relationship = relationship;
    }
    public String getRelation(){return relationship;}
    public void setRelation(String relation){
        this.relationship = relation;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
