package com.uithealthcare.domain.appointment;
import java.io.Serializable;


public class AppointmentRequest implements Serializable  {
    public static final String EXTRA = "extra_appointment_request";

    private String slotId;
    private String service;
    private String careProfileId;

    public AppointmentRequest() {}

    // Getters/Setters
    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getCareProfileId() { return careProfileId; }
    public void setCareProfileId(String careProfileId) { this.careProfileId = careProfileId; }


}
