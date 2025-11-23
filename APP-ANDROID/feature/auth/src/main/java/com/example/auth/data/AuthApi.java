package com.example.auth.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.google.gson.annotations.SerializedName;

public interface AuthApi {

    // ---- request/response models ----
    class LoginReq {
        public String email;
        public String password;
        public LoginReq(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    // Chỉnh tên field token cho khớp JSON thực tế (accessToken / token)
    class LoginResp {
        public boolean success;
        public String message;
        public Data data;

        public static class Data {
            @SerializedName(value = "accessToken", alternate = {"token"})
            public String accessToken;

            public String refreshToken;

            @SerializedName(value = "email", alternate = {"userEmail"})
            public String email;

            // thêm user nếu muốn map luôn thông tin user
            public User user;
        }

        public static class User {
            public String id;
            public String email;
            public String fullName;
            public String role;
        }
    }


    // ---- endpoints ----
    @POST("api/auth/login")
    Call<LoginResp> login(@Body LoginReq body);
    // ---- REGISTER ----
    @POST("api/auth/register")
    Call<RegisterResp> register(@Body RegisterReq body);

    @POST("api/auth/login")
    Call<com.google.gson.JsonObject> loginRaw(@Body LoginReq body);

    @POST("api/auth/forgot")
    Call<ForgotPasswordResp> forgotPassword(@Body ForgotPasswordReq body);

    @POST("api/auth/reset")
    Call<ResetPasswordResp> resetPassword(@Body ResetPasswordReq body);


    class ForgotPasswordReq{
        public String email;

        public ForgotPasswordReq(String email){
            this.email = email;
        }
    }
    class ForgotPasswordResp{
        public boolean success;
        public String message;
    }
    class ResetPasswordReq{
        @SerializedName("email")
        public String email;

        @SerializedName("code")
        public String code;

        @SerializedName("newPassword")
        public String newPassword;

        public ResetPasswordReq(String email, String code, String newPassword) {
            this.email = email;
            this.code = code;
            this.newPassword = newPassword;
        }
    }
    class ResetPasswordResp{
        public boolean success;
        public String message;
    }

    class RegisterReq {
        public String email;
        public String password;
        public String fullName;
        public String role; // mặc định "PATIENT"

        public RegisterReq(String email, String password, String fullName, String role) {
            this.email = email; this.password = password; this.fullName = fullName; this.role = role;
        }
    }

    class RegisterResp {
        public boolean success;
        public String message;
    }
}
