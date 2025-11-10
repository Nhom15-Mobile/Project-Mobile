package com.example.appointment.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.model.ItemRecord;
import com.example.appointment.adapter.RecordAdapter;
import com.example.appointment.service.CareProfileService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AppointmentActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private String TOKEN = null;

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

        sp = getSharedPreferences("app_prefs", MODE_PRIVATE); // OK, context đã có
        TOKEN = sp.getString("access_token", null);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        MaterialButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        MaterialButton btnCreateRecord = findViewById(R.id.btnCreateRecord);
        btnCreateRecord.setOnClickListener(v ->{
            Intent data = new Intent(this, CreateProfileActivity.class);
            startActivity(data);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecordAdapter adapter = new RecordAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        CareProfileService.showListItemRecord(TOKEN, new CareProfileService.CareProfileCallback() { ; // Đổ dữ liệu các record đây
            @Override
            public void onSuccess(List<ItemRecord> items) {
                adapter.setItems(items);      // viết setter trong adapter
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable t) {
                Log.e("API", "load records failed", t);
                // hiện Toast nếu cần
            }
        });



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
