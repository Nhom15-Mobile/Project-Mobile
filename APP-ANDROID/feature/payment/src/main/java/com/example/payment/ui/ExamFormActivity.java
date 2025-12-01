package com.example.payment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.payment.R;
import com.google.android.material.button.MaterialButton;
import com.uithealthcare.domain.appointment.AppointmentInfo;

public class ExamFormActivity extends AppCompatActivity {

    private AppointmentInfo appointmentInfo;
    TextView tvAppointmentId, tvPatientName, tvSpecialty, tvExamDate, tvExamTime, tvClinic, tvFee, tvCreatedDate;
    MaterialButton btnBackHome, btnScreenshot, btnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();

        tvAppointmentId.setText(appointmentInfo.getId());
        tvPatientName.setText(appointmentInfo.getPatientName());
        tvSpecialty.setText(appointmentInfo.getSpecialty());
        tvExamDate.setText(appointmentInfo.getExamDate());
        tvExamTime.setText(appointmentInfo.getExamHour());
        tvClinic.setText(appointmentInfo.getClinic());
        tvFee.setText(appointmentInfo.getPrice());
        tvCreatedDate.setText(appointmentInfo.getCreatedDate());

        btnBackHome.setOnClickListener(v -> {
            finish();
        });
        btnBack.setOnClickListener(v -> finish());
    }

    private void initLayout(){
        setContentView(R.layout.exam_form_activity);

        appointmentInfo = (AppointmentInfo) getIntent().getSerializableExtra(AppointmentInfo.EXTRA);

        tvAppointmentId = findViewById(R.id.tvAppointmentId);
        tvPatientName = findViewById(R.id.tvPatientName);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvExamDate = findViewById(R.id.tvDate);
        tvExamTime = findViewById(R.id.tvTime);
        tvClinic = findViewById(R.id.tvClinic);
        tvFee = findViewById(R.id.tvFee);
        tvCreatedDate= findViewById(R.id.tvBookingDate);

        btnBackHome = findViewById(R.id.btnBackHome);
        btnScreenshot = findViewById(R.id.btnTakePhoto);
        btnBack = findViewById(R.id.btnBack);
    }
}
