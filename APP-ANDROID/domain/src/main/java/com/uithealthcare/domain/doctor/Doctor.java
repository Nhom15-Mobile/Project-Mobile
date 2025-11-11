package com.uithealthcare.domain.doctor;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Doctor{
    @SerializedName("doctorUserId")   private String doctorUserId;
    @SerializedName("doctorProfileId")private String doctorProfileId;
    @SerializedName("fullName")       private String fullName;
    @SerializedName("email")          private String email;
    @SerializedName("specialty")      private String specialty;
    @SerializedName("clinicName")     private String clinicName;
    @SerializedName("yearsExperience")private int yearsExperience;
    @SerializedName("rating")         private float rating;
    @SerializedName("fee")            private long fee; // 150000 -> long
    @SerializedName("Slots")     private List<Slot> slots;

    // getters
    public String getDoctorUserId() { return doctorUserId; }
    public String getDoctorProfileId() { return doctorProfileId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getSpecialty() { return specialty; }
    public String getClinicName() { return clinicName; }
    public int getYearsExperience() { return yearsExperience; }
    public float getRating() { return rating; }
    public long getFee() { return fee; }
    public List<Slot> getSlots() { return slots; }
}

