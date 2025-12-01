package com.example.notification.ui;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notification.R;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ReceiptDetailActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private TextView tvReceiptNo, tvPatientName, tvSpecialty,
            tvExamDate, tvExamTime, tvClinicRoom, tvAmount, tvBookedAt, tvReceiptStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_detail);

        //  back
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Bind views
        tvReceiptStatus = findViewById(R.id.tvReceiptStatus);
        tvReceiptNo     = findViewById(R.id.tvReceiptNo);
        tvPatientName   = findViewById(R.id.tvPatientName);
        tvSpecialty     = findViewById(R.id.tvSpecialty);
        tvExamDate      = findViewById(R.id.tvExamDate);
        tvExamTime      = findViewById(R.id.tvExamTime);
        tvClinicRoom    = findViewById(R.id.tvClinicRoom);
        tvAmount        = findViewById(R.id.tvAmount);
        tvBookedAt      = findViewById(R.id.tvBookedAt);

        // Lấy dữ liệu từ Intent
        String receiptNo   = getIntent().getStringExtra("receiptNo");
        String patientName = getIntent().getStringExtra("patientName");
        String specialty   = getIntent().getStringExtra("specialty");
        String examDate    = getIntent().getStringExtra("examDate");
        String start       = getIntent().getStringExtra("examStart");
        String end         = getIntent().getStringExtra("examEnd");
        String clinicRoom  = getIntent().getStringExtra("clinicRoom");
        int amount         = getIntent().getIntExtra("amount", 0);
        String bookedAt    = getIntent().getStringExtra("bookedAt");

        // Set UI
        tvReceiptStatus.setText("Thanh toán thành công"); // có thể set động theo API nếu muốn

        tvReceiptNo.setText("Mã phiếu: " + n(receiptNo));
        tvPatientName.setText("Bệnh nhân: " + n(patientName));
        tvSpecialty.setText("Chuyên khoa: " + n(specialty));

        tvExamDate.setText("Ngày khám: " + formatDate(examDate));
        tvExamTime.setText("Giờ khám: " + formatTimeRange(start, end));
        tvClinicRoom.setText("Phòng khám: " + n(clinicRoom));

        tvAmount.setText("Số tiền: " + formatAmount(amount));
        tvBookedAt.setText("Đặt lúc: " + formatDateTime(bookedAt));
    }

    private String n(String s) {
        return s == null ? "-" : s;
    }

    // ===== Format helpers (nếu bạn chưa bật desugaring, có thể đổi sang SimpleDateFormat) ====

    private String formatDate(String iso) {
        if (iso == null) return "-";
        Instant ins = Instant.parse(iso);
        return ins.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatDateTime(String iso) {
        if (iso == null) return "-";
        Instant ins = Instant.parse(iso);
        return ins.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }

    private String formatTimeRange(String startIso, String endIso) {
        if (startIso == null || endIso == null) return "-";
        Instant s = Instant.parse(startIso);
        Instant e = Instant.parse(endIso);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        return f.format(s) + " - " + f.format(e);
    }

    private String formatAmount(int amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " đ";
    }
}
