package com.example.appointment.model;

import java.util.List;

public class DoctorSchedule {
    public final String doctorName;
    public final String dateText;
    public final String location;
    public final List<TimeSlot> slots;
    public DoctorSchedule(String name, String dateText, String location, List<TimeSlot> slots) {
        this.doctorName = name;
        this.dateText = dateText;
        this.location = location; this.slots = slots;
    }


}
