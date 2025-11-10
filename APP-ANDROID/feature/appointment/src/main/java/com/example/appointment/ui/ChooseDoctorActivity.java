package com.example.appointment.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.adapter.DoctorScheduleAdapter;
import com.example.appointment.model.DoctorSchedule;
import com.example.appointment.model.TimeSlot;
import com.example.appointment.adapter.TimeSlotAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.*;

public class ChooseDoctorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_doctor_activity);

        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        RecyclerView rvDoctors = findViewById(R.id.rvDoctors);
        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        rvDoctors.setHasFixedSize(true);

        List<DoctorSchedule> list = new ArrayList<>();
// Fake data
        List<TimeSlot> s = Arrays.asList(
                new TimeSlot("6:30 - 7:30", true),
                new TimeSlot("6:30 - 7:30", true),
                new TimeSlot("6:30 - 7:30", true),
                new TimeSlot("6:30 - 7:30", false), // không chọn được
                new TimeSlot("6:30 - 7:30", true),
                new TimeSlot("6:30 - 7:30", true),
                new TimeSlot("6:30 - 7:30", false),
                new TimeSlot("6:30 - 7:30", false),
                new TimeSlot("6:30 - 7:30", false),
                new TimeSlot("6:30 - 7:30", false),
                new TimeSlot("6:30 - 7:30", false)
        );
        list.add(new DoctorSchedule("Ths BS. Nguyễn Thanh A",
                "16/10/2025", "Tòa B - Tầng 2 - Phòng 2.12", s));

        list.add(new DoctorSchedule("Ths BS. Nguyễn Thanh B",
                "16/10/2025", "Tòa A - Tầng 3 - Phòng 3.10", s));

        list.add(new DoctorSchedule("Ths BS. Nguyễn Thanh C",
                "16/10/2025", "Tòa D - Tầng 1 - Phòng 1.2", s));

        list.add(new DoctorSchedule("Ths BS. Nguyễn Thanh D",
                "16/10/2025", "Tòa E - Tầng 5 - Phòng 5.6", s));

        DoctorScheduleAdapter adapter = new DoctorScheduleAdapter(list, (doctor, slot) -> {
            // TODO: xử lý khi chọn khung giờ
            // Ví dụ: trả về Activity trước
            // Intent data = new Intent(); data.putExtra("slot", slot.label);
            // setResult(RESULT_OK, data); finish();
        });
        rvDoctors.setAdapter(adapter);

    }
}
