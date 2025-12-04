package com.example.appointment.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

import com.uithealthcare.domain.careProfile.CareProfilesResponse;
import com.uithealthcare.domain.careProfile.CreateCareProfileRequest;
import com.uithealthcare.domain.careProfile.CreateCareProfileResponse;
import com.uithealthcare.network.ApiConfig;

public interface CareProfileService {

    @GET("api/care-profiles")
    Call<CareProfilesResponse> showOnCardCareProfile();

    @POST("api/care-profiles")
    Call<CreateCareProfileResponse> createCareProfile(@Body CreateCareProfileRequest request);
}
