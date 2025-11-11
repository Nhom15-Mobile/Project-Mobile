package com.example.appointment.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appointment.R;
import com.example.appointment.api.AppointmentService;
import com.google.android.material.button.MaterialButton;
import com.uithealthcare.domain.appointment.AppointmentRequest;
import com.uithealthcare.domain.appointment.AppointmentResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingAppointmentActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private String TOKEN = null;
    private AppointmentRequest req;
    MaterialButton btnBooking;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_appointment);

        sp = getSharedPreferences("app_prefs", MODE_PRIVATE); // OK, context đã có
        TOKEN = sp.getString("access_token", null);

        req = (AppointmentRequest) getIntent().getSerializableExtra(AppointmentRequest.EXTRA);
        Log.d("Req", "Oke");
//        btnBooking.setOnClickListener(v-> booking());
    }

    private void booking(){
        AppointmentService.appointmentService.bookAppointment(TOKEN, req).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Đặt lịch thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
