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
import com.example.appointment.adapter.RecordAdapter;
import com.example.appointment.api.SpecialtyService;
import com.example.appointment.model.ItemRecord;
import com.example.appointment.model.ItemSpecialty;
import com.example.appointment.adapter.SpecialtyAdapter;
import com.google.android.material.button.MaterialButton;
import com.uithealthcare.domain.appointment.AppointmentRequest;
import com.uithealthcare.domain.careProfile.CareProfile;
import com.uithealthcare.domain.careProfile.CareProfilesResponse;
import com.uithealthcare.domain.specialty.Specialty;
import com.uithealthcare.domain.specialty.SpecialtyRespone;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialtyActivity extends AppCompatActivity {

    private SharedPreferences sp;
    private String TOKEN = null;

    private RecyclerView rV;
    private MaterialButton btnBack;

    private List<ItemSpecialty> itemSpecialty;

    private AppointmentRequest req;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specialty_activity);
        req = (AppointmentRequest) getIntent().getSerializableExtra(AppointmentRequest.EXTRA);
        rV = findViewById(R.id.specialtyRecyclerView);
        rV.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        itemSpecialty = new ArrayList<>();
        showOnSpecialtyCard();
    }

    private void showOnSpecialtyCard(){
        SpecialtyService.specialtyService.getListSpecialty().enqueue(new Callback<SpecialtyRespone>() {
            @Override
            public void onResponse(Call<SpecialtyRespone> call, Response<SpecialtyRespone> response) {
                SpecialtyRespone data = response.body();
                if(data != null && data.isSuccess()){
                    List<Specialty> list = data.getData();
                    for (Specialty spec : list){
                        itemSpecialty.add(new ItemSpecialty(spec.getName(), spec.getFee()));
                    }
                }
                SpecialtyAdapter adapter = new SpecialtyAdapter(itemSpecialty);
                rV.setAdapter(adapter);
                adapter.setOnSpecialtyClickListener(item -> {
                    Intent i = new Intent(SpecialtyActivity.this, ChooseDateActivity.class);
                    req.setService(item.getName());
                    i.putExtra(AppointmentRequest.EXTRA, req);
                    i.putExtra("nameSpecialty", item.getName());
                    Log.d("Req", "Đã có specialty: "+ item.getName());
                    startActivity(i);
                });
            }

            @Override
            public void onFailure(Call<SpecialtyRespone> call, Throwable t) {
                Log.d("API", "showOSpecialtyCare Failure");
            }
        });
    }
}
