package com.example.results.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.results.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailResultActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvTitle;

    private TextView tvService;
    private TextView tvExamDate;
    private TextView tvStatus;

    private TextView tvExamResult;

    private TextView tvCareProfileName;
    private TextView tvRelation;
    private TextView tvDoctorName;
    private TextView tvRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đặt đúng tên file layout bạn lưu (vd: detail_result.xml)
        setContentView(R.layout.detail_results);

        initViews();
        bindDataFromIntent();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);

        tvService = findViewById(R.id.tvService);
        tvExamDate = findViewById(R.id.tvExamDate);
        tvStatus = findViewById(R.id.tvStatus);
        //tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        tvExamResult = findViewById(R.id.tvExamResult);


        tvCareProfileName = findViewById(R.id.tvCareProfileName);
        tvRelation = findViewById(R.id.tvRelation);
        tvDoctorName = findViewById(R.id.tvDoctorName);

        tvRecommendation = findViewById(R.id.tvRecommendation);

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void bindDataFromIntent() {
        Intent intent = getIntent();
        if (intent == null) return;

        // Lấy dữ liệu từ Intent (phải trùng key với bên ChooseResultActivity)
        String service = intent.getStringExtra("service");
        String scheduledAt = intent.getStringExtra("scheduledAt");
        String status = intent.getStringExtra("status");
        //String paymentStatus = intent.getStringExtra("paymentStatus");
        String examResult = intent.getStringExtra("examResult");


        String careProfileName = intent.getStringExtra("careProfileName");
        String relation = intent.getStringExtra("relation");
        String doctorName = intent.getStringExtra("doctorName");
        String RecommendationLabel = intent.getStringExtra("recommend");


        // Set dữ liệu vào UI
        tvService.setText("Dịch vụ: " + safeText(service));
        tvExamDate.setText("Thời gian khám: " + formatDate(scheduledAt));
        tvStatus.setText("Trạng thái: " + safeText(status));
//        tvPaymentStatus.setText("Thanh toán: " + safeText(paymentStatus));
        tvExamResult.setText(safeText(examResult));

        tvCareProfileName.setText("Hồ sơ khám: " + safeText(careProfileName));
        tvRelation.setText("Quan hệ: " + safeText(relation));

        tvDoctorName.setText("Tên bác sĩ: " + safeText(doctorName));
        tvRecommendation.setText(safeText(RecommendationLabel));
    }

    private String safeText(String s) {
        return s == null ? "Không có thông tin" : s;
    }

    private String formatDate(String iso) {
        if (iso == null) return "Không rõ";

        try {
            // ví dụ: 2025-12-16T06:30:00.000Z
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = input.parse(iso);

            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return output.format(date);
        } catch (ParseException e) {
            // Nếu parse lỗi thì trả raw luôn
            return iso;
        }
    }
}
