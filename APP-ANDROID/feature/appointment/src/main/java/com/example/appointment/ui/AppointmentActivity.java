package com.example.appointment.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.model.ItemRecord;
import com.example.appointment.adapter.RecordAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {

    public List<ItemRecord> fakeData(){
        List<ItemRecord> items = new ArrayList<ItemRecord>();
        for (int i = 1; i <= 5; i++) {
            String name = "Nguyễn Văn " + (char)('A' + i); // Nguyễn Văn A, B, C...
            String idRecord = "K23-2352" + String.format("%04d", i);
            String phone = "0888xxx" + String.format("%03d", i);

            items.add(new ItemRecord(name, idRecord, phone));
        }
        return items;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_activity);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        List<ItemRecord> items = fakeData(); // Đổ dữ liệu các record đây

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecordAdapter adapter = new RecordAdapter(this, items);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Intent i = new Intent(AppointmentActivity.this, SpecialtyActivity.class);
            // Truyền dữ liệu cần thiết sang màn đặt lịch:
            i.putExtra("name", item.getName());
            i.putExtra("id", item.getId());
            i.putExtra("phone", item.getPhone());
            startActivity(i);

            // nếu muốn đóng luôn màn hiện tại:
            // finish();
        });
    }
}
