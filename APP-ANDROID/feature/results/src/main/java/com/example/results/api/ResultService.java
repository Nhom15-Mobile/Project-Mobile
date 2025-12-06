package com.example.results.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uithealthcare.domain.result.ResultResponse;
import com.uithealthcare.network.ApiConfig;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ResultService {

    Gson gson = new GsonBuilder().create();
    ResultService RESULT_SERVICE =  new Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ResultService.class);
    @GET("/api/patient/appointments/results")
    Call<ResultResponse> getResults(@Header("Authorization") String bearerToken);

}
