package com.example.auth.data;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthRepository {

    public interface LoginCallback {
        void onSuccess(AuthApi.LoginResp.Data data);
        void onError(String errorMessage);
    }

    private final AuthApi api;

    public AuthRepository(Context ctx) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://uithealthcare.id.vn/") // base URL của bạn
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        api = retrofit.create(AuthApi.class);
    }

    public void login(String email, String password, LoginCallback cb) {
        AuthApi.LoginReq req = new AuthApi.LoginReq(email, password);
        api.login(req).enqueue(new Callback<AuthApi.LoginResp>() {
            @Override
            public void onResponse(Call<AuthApi.LoginResp> call, Response<AuthApi.LoginResp> response) {
                if (response.isSuccessful() && response.body() != null && response.body().success) {
                    cb.onSuccess(response.body().data);
                } else {
                    String msg = "Login failed";
                    if (response.body() != null && response.body().message != null) {
                        msg = response.body().message;
                    }
                    cb.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<AuthApi.LoginResp> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }
    public interface RegisterCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void register(String fullName, String email, String password, RegisterCallback cb) {
        AuthApi.RegisterReq req = new AuthApi.RegisterReq(email, password, fullName, "PATIENT");
        api.register(req).enqueue(new Callback<AuthApi.RegisterResp>() {
            @Override
            public void onResponse(Call<AuthApi.RegisterResp> call, Response<AuthApi.RegisterResp> res) {
                if (res.isSuccessful() && res.body() != null && res.body().success) {
                    cb.onSuccess(res.body().message != null ? res.body().message : "Registered");
                } else {
                    String msg = (res.body()!=null && res.body().message!=null) ? res.body().message : "Register failed";
                    cb.onError(msg);
                }
            }
            @Override public void onFailure(Call<AuthApi.RegisterResp> call, Throwable t) { cb.onError(t.getMessage()); }
        });
    }
}
