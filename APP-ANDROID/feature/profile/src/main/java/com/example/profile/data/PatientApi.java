package com.example.profile.data;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Body;

public interface PatientApi {

    @GET("api/patient/profile")
    Call<ProfileResp> getMyProfile();

    @PATCH("api/patient/profile")
    Call<UpdateResp> updateMyProfile(@Body UpdateReq body);

    // ===== models =====
    class ProfileResp {
        public boolean success;
        public String message;
        public Data data;               // nhiều API bọc data
        // Một số API trả trực tiếp user ở root:
        @SerializedName("user") public Patient userRoot;
        // hoặc trả trực tiếp profile:
        @SerializedName("profile") public Patient profileRoot;
    }

    class Data {
        // /api/patient/profile thường trả user trong data
        @SerializedName("user") public Patient user;
        @SerializedName("profile") public Patient profile;
    }

    public static class Patient {
        // tên thường gặp: fullName | full_name | name
        @SerializedName(value="fullName", alternate={"full_name","name"})
        public String fullName;

        // gender: male/female/other hoặc "Nam/Nữ"
        @SerializedName("gender")
        public String gender;

        // dob: yyyy-MM-dd hoặc yyyy/MM/dd
        @SerializedName(value="dob", alternate={"dateOfBirth","birthDate"})
        public String dob;

        // phone: phone | phone_number
        @SerializedName(value="phone", alternate={"phone_number","mobile"})
        public String phone;

        // address
        @SerializedName(value="address", alternate={"addr","homeAddress"})
        public String address;

        // email
        @SerializedName("email")
        public String email;

        // avatar url
        @SerializedName(value="avatarUrl", alternate={"avatar","avatar_url","photo"})
        public String avatarUrl;
    }

    public static class UpdateReq {
        public String fullName, gender, dob, phone, address, email;
        public UpdateReq(String fullName, String gender, String dob,
                         String phone, String address, String email) {
            this.fullName = fullName; this.gender = gender; this.dob = dob;
            this.phone = phone; this.address = address; this.email = email;
        }
    }

    public static class UpdateResp {
        public boolean success;
        public String message;
        @SerializedName("data") public Patient data; // nếu server trả lại profile đã cập nhật
    }
}
