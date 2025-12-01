package com.example.notification.data;

import java.util.List;

//import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationApi {

    // GET /api/notifications?cursor=&limit=&unreadOnly=
    @GET("api/notifications")
    Call<NotificationListResp> getNotifications(
            @Query("cursor") String cursor,
            @Query("limit") Integer limit,
            @Query("unreadOnly") Boolean unreadOnly
    );

    // ====== models ======
    class NotificationListResp {
        public boolean success;
        public String message;
        public Data data;
    }

    public static class Data {
        public List<NotificationDto> items;
        public String nextCursor;
    }

    // 1 item notification server trả
    public static class NotificationDto {
        public String id;
        public String userId;
        public String type;     // PAYMENT_SUCCESS, ...
        public String title;    // "Thanh toán thành công"
        public String body;     // "Bạn đã thanh toán thành công lịch khám ..."
        public NotiData data;   // { appointmentId, scheduledAt, service }
        public String readAt;   // null nếu chưa đọc
        public String createdAt;
    }

    public static class NotiData {
        public String appointmentId;
        public String scheduledAt;
        public String service;
    }

}
