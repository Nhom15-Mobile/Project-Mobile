package com.example.appointment.ui;

import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.model.ItemRecord;
import com.example.appointment.model.RecordAdapter;

import java.util.ArrayList;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {

    public List<ItemRecord> fakeData(){
        List<ItemRecord> items = new ArrayList<ItemRecord>();
        for (int i = 1; i <= 15; i++) {
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

        List<ItemRecord> items = fakeData(); // Đổ dữ liệu các record đây

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecordAdapter(this, items));
    }
}
