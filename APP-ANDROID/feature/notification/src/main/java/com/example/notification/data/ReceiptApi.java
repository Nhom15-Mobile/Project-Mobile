package com.example.notification.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReceiptApi {
    @GET("api/payments/receipt/{id}")
    Call<ReceiptResp> getReceipt(@Path("id") String appointmentid);

    class ReceiptResp {
        public boolean success;
        public String message;
        public Receipt data;
    }

    public static class Receipt {
        public String receiptNo;
        public String patientName;
        public String specialty;
        public String examDate;
        public ExamTime examTime;
        public String clinicRoom;
        public int amount;
        public String bookedAt;
    }

    public static class ExamTime {
        public String start;
        public String end;
    }
}
