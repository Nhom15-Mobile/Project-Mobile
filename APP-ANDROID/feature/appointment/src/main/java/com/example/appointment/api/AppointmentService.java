package com.example.appointment.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.uithealthcare.domain.appointment.AppointmentHistoryResponse;
import com.uithealthcare.domain.appointment.AppointmentRequest;
import com.uithealthcare.domain.appointment.AppointmentResponse;
import com.uithealthcare.network.ApiConfig;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AppointmentService {

    @POST("api/appointments/book")
    Call<AppointmentResponse> bookAppointment(@Body AppointmentRequest request);

    @GET("api/patient/appointments")
    Call<AppointmentHistoryResponse> getAppointments();
}
