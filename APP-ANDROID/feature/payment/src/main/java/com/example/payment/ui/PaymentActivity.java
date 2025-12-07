package com.example.payment.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



import com.example.payment.api.PaymentService;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.uithealthcare.domain.appointment.AppointmentData;
import com.uithealthcare.domain.appointment.AppointmentInfo;
import com.uithealthcare.domain.payment.Payment;
import com.uithealthcare.domain.payment.PaymentRequest;
import com.uithealthcare.domain.payment.PaymentResponse;
import com.uithealthcare.domain.payment.ProcessPaymentResponse;
import com.uithealthcare.network.ApiServices;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.SessionManager;
import com.example.payment.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaymentActivity extends AppCompatActivity {

    AppointmentInfo appointmentInfo;
    ImageView qrImage;
    MaterialButton btnProcessPayment, btnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();

        appointmentInfo = (AppointmentInfo) getIntent().getSerializableExtra(AppointmentInfo.EXTRA);

        SessionManager manager = new SessionManager(this);

        SessionInterceptor.TokenProvider tokenProvider = new SessionInterceptor.TokenProvider() {
            @Override
            public String getToken() {
                return manager.getBearer();
            }
        };

        PaymentService paymentService = ApiServices.create(PaymentService.class, tokenProvider);
        createQr(paymentService);

        btnBack.setOnClickListener(v -> finish());
        btnProcessPayment.setOnClickListener(v -> payment(paymentService));
    }

    private void initLayout(){
        setContentView(R.layout.payment_activity);

        qrImage = findViewById(R.id.qrImage);
        btnProcessPayment = findViewById(R.id.btnProcessPayment);
        btnBack = findViewById(R.id.btnBack);
    }

    private void createQr(PaymentService paymentService){
        if (appointmentInfo != null){
            PaymentRequest request = new PaymentRequest(appointmentInfo.getId());
            paymentService.createMomoPayment(request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                PaymentResponse mResponse = response.body();
                if (mResponse != null && mResponse.isSuccess()) {
                    Payment paymentData = mResponse.getData();
                    String qrImageDataUrl = paymentData.getQrImage();
                    generateQrImage(qrImageDataUrl);

                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable throwable) {

            }
            });
        }
    }

    private void generateQrImage(String imageUrl){
        // 1. Cắt bỏ "data:image/png;base64,"
        String base64 = imageUrl;
        int commaIndex = base64.indexOf(",");
        if (commaIndex != -1) {
            base64 = base64.substring(commaIndex + 1);
        }

        // 2. Decode base64 -> byte[]
        byte[] decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);

        // 3. byte[] -> Bitmap
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        // 4. Set vào ImageView
        qrImage.setImageBitmap(bitmap);
    }

    private void payment(PaymentService paymentService){
        if (appointmentInfo != null){
            paymentService.processPayment().enqueue(new Callback<ProcessPaymentResponse>() {
                @Override
                public void onResponse(Call<ProcessPaymentResponse> call, Response<ProcessPaymentResponse> response) {
                    if (response.isSuccessful() && response.body() != null){
                        ProcessPaymentResponse mResponse = response.body();
                        List<AppointmentData> listAppoitmentData = mResponse.getData();
                        AppointmentData appointmentData = listAppoitmentData.get(0);

                        if (appointmentData.getId().equals(appointmentInfo.getId())){
                            Intent data = new Intent(PaymentActivity.this, ExamFormActivity.class);
                            data.putExtra(AppointmentInfo.EXTRA, appointmentInfo);
                            startActivity(data);
                            Toast.makeText(getApplicationContext(), "Thanh toán thành công",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Thanh toán thất bại, vui lòng thanh toán lại",
                                    Toast.LENGTH_SHORT).show();
                       }
                    }
//                    if (response.isSuccessful() && response.body() != null){
//                        ProcessPaymentResponse mResponse = response.body();
//                        List<AppointmentData> list = mResponse.getData();
//
//                        Log.d("PAYMENT", "Response: " + new Gson().toJson(mResponse));
//                        Log.d("PAYMENT", "Current appointmentInfo id: " + appointmentInfo.getId());
//
//                        if (list != null && !list.isEmpty()) {
//                            for (AppointmentData ad : list) {
//                                Log.d("PAYMENT", "Item id: " + ad.getId());
//                            }
//                        }
//
//                        // tạm thời bỏ so sánh id để test:
//                        Toast.makeText(getApplicationContext(), "API trả về OK, kiểm tra log", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.e("PAYMENT", "API error: code = " + response.code());
//                        Toast.makeText(getApplicationContext(), "Lỗi kết nối hệ thống thanh toán", Toast.LENGTH_SHORT).show();
//                    }
                }

                @Override
                public void onFailure(Call<ProcessPaymentResponse> call, Throwable throwable) {

                }
            });
        }
    }
}
