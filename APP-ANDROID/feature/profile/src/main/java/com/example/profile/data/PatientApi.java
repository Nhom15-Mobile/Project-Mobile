package com.example.profile.data;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PatientApi {


    // 2) Get Profil
    @GET("api/patient/profile")
    Call<GetProfileResp> getMyProfile();

    // 3) Update
    @POST("api/patient/profile")
    Call<UpdateResp> updateMyProfile(@Body UpdateReq body);


    // --- GET response ---
    class GetProfileResp {
        public boolean success;
        public String message;
        public ProfileData data;
    }


    // --- POST response ---
    class UpdateResp {
        public boolean success;
        public String message;
        public ProfileData data; // server có thể không trả "user" trong data -> user có thể null
    }

    // --- data của profile (khớp JSON bạn gửi) ---
    class ProfileData {
        @SerializedName("userId")           public String userId;

        // ISO dạng "2024-11-09T17:00:00.000Z"
        @SerializedName("dob")              public String dob;

        // "male" | "female" | "other"
        @SerializedName("gender")           public String gender;

        // các trường phụ (có thể null)
        @SerializedName("medicalHistory")   public String medicalHistory;
        @SerializedName("medications")      public String medications;
        @SerializedName("allergies")        public String allergies;

        @SerializedName("insuranceNumber")  public String insuranceNumber;
        @SerializedName("address")          public String address;
        @SerializedName("emergencyContact") public String emergencyContact;

        @SerializedName("createdAt")        public String createdAt;
        @SerializedName("updatedAt")        public String updatedAt;

        // object con user (GET chắc chắn có, POST có thể không trả)
        @SerializedName("user")             public User user;
    }

    // --- user trong data.user ---
    class User {
        @SerializedName("id")        public String id;
        @SerializedName("email")     public String email;
        @SerializedName("fullName")  public String fullName;
        @SerializedName("phone")     public String phone;
        @SerializedName("role")      public String role;
        @SerializedName("createdAt") public String createdAt;
        @SerializedName("updatedAt") public String updatedAt;
        // password có trong JSON nhưng không cần map để hiển thị
    }

    // --- body cập nhật cơ bản ---
    // Tuỳ backend, bạn gửi những field nào muốn cập nhật (null sẽ KHÔNG serialize nếu dùng Gson mặc định).
    // Các key giữ nguyên đúng tên như JSON.
    class UpdateReq {
        @SerializedName("gender")           public String gender;           // "male|female|other"
        @SerializedName("dob")              public String dob;              // "yyyy-MM-dd" (server sẽ lưu ISO)
        @SerializedName("address")          public String address;
        @SerializedName("insuranceNumber")  public String insuranceNumber;
        @SerializedName("emergencyContact") public String emergencyContact;

        // Nếu backend cho phép cập nhật phone qua đây thì thêm:
        @SerializedName("phone")            public String phone;

        // Nếu backend cho phép cập nhật fullName/email qua endpoint này thì thêm:
//        @SerializedName("fullName")         public String fullName;
//        @SerializedName("email")            public String email;

        public UpdateReq(String gender,
                         String dob,
                         String address,
                         String insuranceNumber,
                         String emergencyContact
                         ) {
            this.gender = gender;
            this.dob = dob;
            this.address = address;
            this.insuranceNumber = insuranceNumber;
            this.emergencyContact = emergencyContact;
//            this.phone = phone;
            //this.fullName = fullName;
//            this.email = email;
//
        }

        // Tiện lợi: constructor rút gọn cho các field cơ bản bạn đang dùng
        public static UpdateReq basic(String gender, String dob, String address, String emergencyContact) {
            return new UpdateReq(gender, dob, address, null, emergencyContact );
        }
    }
}
