package com.example.records.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uithealthcare.domain.careProfile.CareProfilesResponse;
import com.uithealthcare.network.ApiConfig;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface CareProfileService {

    Gson gson = new GsonBuilder().create();

    CareProfileService CARE_PROFILE_API =  new Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(CareProfileService.class);


    @GET("api/care-profiles")
    Call<CareProfilesResponse> showOnCardCareProfile(@Header("Authorization") String token);
}
