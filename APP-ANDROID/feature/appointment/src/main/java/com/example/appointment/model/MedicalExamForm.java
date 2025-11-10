package com.example.appointment.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MedicalExamForm implements Parcelable {
    public String patientsName;
    public String specialtyId;
    public String specialtyName;
    public String visitDateUtc;      // hoặc String yyyy-MM-dd nếu bạn muốn
    public String timeSlot;        // "7:30 - 8:30"
    public String doctorId;
    public String doctorName;
    public String clinic;          // "Phòng B3.22 - Toà B - Lầu 3"
    public String bookingDateUtc;    // ngày đặt
    public int fee;                // 150000

    public MedicalExamForm() {}

    public MedicalExamForm(Parcel in) {
        specialtyId = in.readString();
        specialtyName = in.readString();
        visitDateUtc = in.readString();
        timeSlot = in.readString();
        doctorId = in.readString();
        doctorName = in.readString();
        clinic = in.readString();
        bookingDateUtc = in.readString();
        fee = in.readInt();
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(specialtyId);
        dest.writeString(specialtyName);
        dest.writeString(visitDateUtc);
        dest.writeString(timeSlot);
        dest.writeString(doctorId);
        dest.writeString(doctorName);
        dest.writeString(clinic);
        dest.writeString(bookingDateUtc);
        dest.writeInt(fee);
    }
    @Override public int describeContents() { return 0; }

    public static final Creator<MedicalExamForm> CREATOR = new Creator<MedicalExamForm>() {
        @Override public MedicalExamForm createFromParcel(Parcel in) { return new MedicalExamForm(in); }
        @Override public MedicalExamForm[] newArray(int size) { return new MedicalExamForm[size]; }
    };
}


