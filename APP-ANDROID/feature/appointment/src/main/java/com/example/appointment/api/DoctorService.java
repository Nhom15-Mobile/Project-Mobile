package com.example.appointment.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uithealthcare.domain.doctor.DoctorRespone;
import com.uithealthcare.network.ApiConfig;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorService {
    Gson gson = new GsonBuilder().create();

    DoctorService doctorService =  new Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(DoctorService.class);

    @GET("api/doctors/available") //// Gọi tới: api/doctors/available?day=2025-12-11&specialty=MẮT
    Call<DoctorRespone> getAvailableDoctors(
            @Query("day") String day,
            @Query("specialty") String specialty
    );
}
