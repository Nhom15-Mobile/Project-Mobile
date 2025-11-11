package com.uithealthcare.domain.doctor;

import com.google.gson.annotations.SerializedName;
import java.time.*;
import java.time.format.DateTimeFormatter;
public class Slot {
    @SerializedName("id")    String id;
    @SerializedName("start") String start; // ISO UTC
    @SerializedName("end")   String end;

    private static final java.text.SimpleDateFormat ISO =
            new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
    private static final java.text.SimpleDateFormat DATE_OUT =
            new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
    private static final java.text.SimpleDateFormat TIME_OUT =
            new java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

    static {
        ISO.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // parse theo UTC
        // format theo timezone máy (mặc định)
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() throws java.text.ParseException {
        java.util.Date d = ISO.parse(start);
        return DATE_OUT.format(d);
    }

    public String getStartTime() throws java.text.ParseException {
        return TIME_OUT.format(ISO.parse(start));
    }

    public String getEndTime() throws java.text.ParseException {
        return TIME_OUT.format(ISO.parse(end));
    }
}
