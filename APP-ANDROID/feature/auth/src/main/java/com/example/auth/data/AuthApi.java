package com.example.auth.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
            public String accessToken;   // <— nếu API trả "token", đổi thành: public String token;
            public String refreshToken;  // optional
            public String email;         // optional
        }
    }

    // ---- endpoints ----
    @POST("api/auth/login")
    Call<LoginResp> login(@Body LoginReq body);
    // ---- REGISTER ----
    @POST("api/auth/register")
    Call<RegisterResp> register(@Body RegisterReq body);

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
