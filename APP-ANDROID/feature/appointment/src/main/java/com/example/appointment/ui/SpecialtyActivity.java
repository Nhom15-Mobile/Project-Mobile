package com.example.appointment.ui;

import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.model.ItemRecord;
import com.example.appointment.model.ItemSpecialty;
import com.example.appointment.model.SpecialtyAdapter;

import java.util.ArrayList;
import java.util.List;

public class SpecialtyActivity extends AppCompatActivity {

    public List<ItemSpecialty> fakeData(){
        List<ItemSpecialty> items = new ArrayList<ItemSpecialty>();
        for (int i = 1; i <= 15; i++) {
            String name = "Nguyễn Văn " + (char)('A' + i); // Nguyễn Văn A, B, C...
            String price = "150.000";

            items.add(new ItemSpecialty(name, price));
        }
        return items;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specialty_activity);

        RecyclerView rV = findViewById(R.id.specialtyRecyclerView);

        List<ItemSpecialty> items = fakeData();

        rV.setLayoutManager(new LinearLayoutManager(this));
        SpecialtyAdapter adapter = new SpecialtyAdapter(this, items);
        rV.setAdapter(adapter);
    }
}
