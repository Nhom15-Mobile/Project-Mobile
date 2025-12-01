package com.example.notification.data;

public class NotificationItem {
    public String title;
    public String type;
    public String content;
    public String time;
    public boolean isNew;
    public int iconRes;
    public String appointmentId;
    public String service;
    public String scheduledAt;

    public NotificationItem(String title, String content, String time, boolean isNew, int iconRes) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.isNew = isNew;
        this.iconRes = iconRes;
    }
}
