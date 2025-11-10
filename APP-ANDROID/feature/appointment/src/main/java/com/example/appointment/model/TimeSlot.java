package com.example.appointment.model;

public class TimeSlot {
    public final String label;
    public final boolean available;
    public boolean selected;

    public TimeSlot(String label, boolean available) {
        this.label = label;
        this.available = available;
        this.selected = false;
    }
}
