package com.example.appointment.model;

public class TimeSlot {
    private String slotId;
    public final String label;
    public final boolean available;
    public boolean selected;

    public TimeSlot(String slotId, String label, boolean available) {
        this.slotId = slotId;
        this.label = label;
        this.available = available;
        this.selected = false;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
}
