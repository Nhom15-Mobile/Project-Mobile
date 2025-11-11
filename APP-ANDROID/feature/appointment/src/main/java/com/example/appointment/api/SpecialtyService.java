package com.example.appointment.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uithealthcare.domain.specialty.SpecialtyRespone;
import com.uithealthcare.network.ApiConfig;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public interface SpecialtyService {
    Gson gson = new GsonBuilder().create();

    SpecialtyService specialtyService =  new Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(SpecialtyService.class);

    @GET("api/doctors/specialties")
    Call<SpecialtyRespone> getListSpecialty();
}
