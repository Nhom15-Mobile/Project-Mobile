package com.example.appointment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appointment.R;
import com.example.appointment.api.AppointmentService;
import com.google.android.material.button.MaterialButton;
import com.uithealthcare.domain.appointment.AppointmentData;
import com.uithealthcare.domain.appointment.AppointmentInfo;
import com.uithealthcare.domain.appointment.AppointmentRequest;
import com.uithealthcare.domain.appointment.AppointmentResponse;
import com.uithealthcare.network.ApiServices;
import com.uithealthcare.network.SessionInterceptor;
import com.example.payment.ui.PaymentActivity;
import com.uithealthcare.util.SessionManager;

import com.uithealthcare.util.ConvertDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingAppointmentActivity extends AppCompatActivity {
    private AppointmentRequest req;
    private AppointmentInfo appointmentInfo;
    MaterialButton btnBooking;
    MaterialButton btnBack;

    TextView tvSpecialty, tvDate, tvTime, tvClinic, tvPrice;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();

        SessionManager manager = new SessionManager(this);

        SessionInterceptor.TokenProvider tokenProvider = new SessionInterceptor.TokenProvider() {
            @Override
            public String getToken() {
                return manager.getBearer();
            }
        };

        AppointmentService appointmentService =
                ApiServices.create(AppointmentService.class, tokenProvider);

        tvSpecialty.setText(appointmentInfo.getSpecialty());
        tvDate.setText(ConvertDate.UStoDateVN(appointmentInfo.getExamDate()));
        tvTime.setText(appointmentInfo.getExamHour());
        tvClinic.setText(appointmentInfo.getClinic());
        tvPrice.setText(appointmentInfo.getPrice());

        btnBooking.setOnClickListener(v-> booking(appointmentService));
    }

    private void initLayout(){
        setContentView(R.layout.booking_appointment);
        req = (AppointmentRequest) getIntent().getSerializableExtra(AppointmentRequest.EXTRA);
        appointmentInfo = (AppointmentInfo) getIntent().getSerializableExtra(AppointmentInfo.EXTRA);

        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvClinic = findViewById(R.id.tvClinic);
        tvPrice = findViewById(R.id.tvPrice);

        btnBooking = findViewById(R.id.btnBooking);
        btnBack = findViewById(R.id.btnBack);
    }

    private void booking(AppointmentService appointmentService){
        appointmentService.bookAppointment(req).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful()) {
                    AppointmentResponse myResponse = response.body();
                    if(myResponse != null && myResponse.isSuccess()){
                        AppointmentData appointmentData = myResponse.getData();
                        appointmentInfo.setId(appointmentData.getId());
                        appointmentInfo.setCreatedDate(ConvertDate.ISOtoDateUS(appointmentData.getCreatedAt()));
                        Toast.makeText(getApplicationContext(), myResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    Intent data = new Intent(BookingAppointmentActivity.this, PaymentActivity.class);
                    data.putExtra(AppointmentInfo.EXTRA, appointmentInfo);
                    startActivity(data);
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
