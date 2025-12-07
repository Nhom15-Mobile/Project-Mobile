package com.uithealthcare.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitProvider {
    private RetrofitProvider(){}

    private static Retrofit retrofit;

    public static Retrofit get(SessionInterceptor.TokenProvider tokenProvider){
        if (retrofit == null){
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new SessionInterceptor(tokenProvider))
                    .addInterceptor(log)
                    .connectTimeout(30, TimeUnit.SECONDS)  // thời gian chờ connect
                    .writeTimeout(60, TimeUnit.SECONDS)    // upload ảnh
                    .readTimeout(120, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder().setLenient().create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiConfig.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
    public static synchronized void reset() {
        retrofit = null;
    }
}
