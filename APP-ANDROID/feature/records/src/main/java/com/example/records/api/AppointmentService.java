package com.example.records.api;

import com.uithealthcare.domain.appointment.AppointmentHistoryResponse;
import com.uithealthcare.domain.appointment.AppointmentRequest;
import com.uithealthcare.domain.appointment.AppointmentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AppointmentService {

    @POST("api/appointments/book")
    Call<AppointmentResponse> bookAppointment(@Body AppointmentRequest request);

    @GET("api/patient/appointments")
    Call<AppointmentHistoryResponse> getAppointments();
}
