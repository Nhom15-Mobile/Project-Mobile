package com.example.payment.api;

import com.uithealthcare.domain.payment.PaymentRequest;
import com.uithealthcare.domain.payment.PaymentResponse;
import com.uithealthcare.domain.payment.ProcessPaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PaymentService {
    @POST("api/payments/momo/create")
    Call<PaymentResponse> createMomoPayment(
            @Body PaymentRequest request
    );

    @GET("api/patient/appointments/paid")
    Call<ProcessPaymentResponse> processPayment();
}
