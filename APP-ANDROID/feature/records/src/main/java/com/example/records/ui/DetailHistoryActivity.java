package com.example.records.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.records.R;
import com.example.records.model.AppointmentHistoryItem;

public class DetailHistoryActivity extends AppCompatActivity {

    private TextView tvStatusChip, tvService, tvDateTime, tvDoctorName, tvPatientName, tvPaymentInfo, tvExamTimeDetail, tvExamDateDetail, tvCreatedAt;
    private TextView tvPaymentAmount,tvPaymentTransId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_appointment_his_1);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvStatusChip      = findViewById(R.id.tvStatusChip);
        tvService      = findViewById(R.id.tvService);
        tvDateTime     = findViewById(R.id.tvDateTime);
        tvDoctorName   = findViewById(R.id.tvDoctorName);
        tvPatientName  = findViewById(R.id.tvCareProfileName);
        tvPaymentInfo  = findViewById(R.id.tvPaymentStatus);
        tvExamDateDetail = findViewById(R.id.tvExamDateDetail);
        tvExamTimeDetail = findViewById(R.id.tvExamTimeDetail);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvPaymentAmount = findViewById(R.id.tvPaymentAmount);
        tvPaymentTransId = findViewById(R.id.tvPaymentTransId);

        AppointmentHistoryItem item =
                (AppointmentHistoryItem) getIntent().getSerializableExtra("EXTRA_APPOINTMENT_ITEM");

        if (item == null) return;

        // Set thông tin khám
        tvService.setText(item.getSpecialtyName());
        tvDoctorName.setText(item.getDoctorName());
        tvPatientName.setText(item.getPatientName());
        tvExamDateDetail.setText("Ngày khám: " + item.getDate());
        tvExamTimeDetail.setText("Giờ khám: " + item.getTime());


        // tổng quan

        tvDateTime.setText(item.getDate() + " • " + item.getTime());
        tvCreatedAt.setText(item.getCreateAt());


        // thanh toán
        tvPaymentInfo.setText(item.getPaymentStatus()); // hoặc format “Đã thanh toán 150.000đ”
        // --- Set trạng thái (chưa khám / đã khám) dựa trên item.getStatus() + thời gian ---
        // Trong mapToHistoryItem bạn đã set:
        // item.setStatus("UPCOMING") hoặc "DONE"
        String uiStatus = "Chưa khám";
        if ("DONE".equalsIgnoreCase(item.getStatus())) {
            uiStatus = "Đã khám";
        }
        tvStatusChip.setText("Trạng thái: " + uiStatus);
        tvPaymentAmount.setText("Số tiền: " + item.getAmount());
        tvPaymentTransId.setText("Mã giao dịch: " + item.getTransId());

    }
}
