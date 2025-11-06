package com.uithealthcare.domain.auth;

public class LoginResponse {
    public boolean success;
    public Data data;

    public static class Data {
        public String token;
        public User user;
    }
    public static class User {
        public String id;
        public String email;
        public String fullName;
        public String role;
    }
}
