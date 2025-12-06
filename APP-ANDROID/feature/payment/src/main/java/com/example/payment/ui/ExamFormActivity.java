package com.example.payment.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.payment.R;
import com.google.android.material.button.MaterialButton;
import com.uithealthcare.domain.appointment.AppointmentInfo;
import com.uithealthcare.util.SaveImage;


public class ExamFormActivity extends AppCompatActivity {

    private AppointmentInfo appointmentInfo;
    TextView tvAppointmentId, tvPatientName, tvSpecialty, tvExamDate, tvExamTime, tvClinic, tvFee, tvCreatedDate;
    MaterialButton btnBackHome, btnTakePhoto, btnBack;

    private View cardAppointment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();

        // mã phiếu gen
        String genCode = generateTicketCode(appointmentInfo.getId());



        tvAppointmentId.setText(genCode);
        tvPatientName.setText(appointmentInfo.getPatientName());
        tvSpecialty.setText(appointmentInfo.getSpecialty());
        tvExamDate.setText(appointmentInfo.getExamDate());
        tvExamTime.setText(appointmentInfo.getExamHour());
        tvClinic.setText(appointmentInfo.getClinic());
        tvFee.setText(appointmentInfo.getPrice());
        tvCreatedDate.setText(appointmentInfo.getCreatedDate());

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setClassName(
                    getPackageName(),
                    "com.example.mobile_app.HomeActivity"
            );
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnTakePhoto.setOnClickListener(v -> {
            // 1. Chụp View thành Bitmap
            Bitmap bitmap = SaveImage.captureViewToBitmap(cardAppointment);

            // 2. Tạo tên file kiểu: phieu_kham_20251206_153000.png
            String fileName = "phieu_kham_" + System.currentTimeMillis() + ".png";

            // 3. Lưu vào Gallery
            SaveImage.saveBitmapToGallery(this, bitmap, fileName);
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
        //nút save
        btnTakePhoto = findViewById(R.id.btnTakePhoto);

        // card phiếu khám để lưu
        cardAppointment = findViewById(R.id.cardAppointment);

        btnBack = findViewById(R.id.btnBack);
    }
    private String generateTicketCode(String appointmentId) {
        // Lấy 4 ký tự cuối từ appointmentId (nếu dài)
        String tail = appointmentId;
        if (appointmentId != null && appointmentId.length() > 4) {
            tail = appointmentId.substring(appointmentId.length() - 4);
        }
//        // Lấy phần ngày: ddMMyy
//        String datePart = new java.text.SimpleDateFormat("ddMMyy", java.util.Locale.getDefault())
//                .format(new java.util.Date());
        return "KVK" + "_" + tail.toUpperCase();
    }

}
