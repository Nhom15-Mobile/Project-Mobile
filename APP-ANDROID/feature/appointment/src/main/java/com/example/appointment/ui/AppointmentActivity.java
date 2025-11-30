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
import com.google.android.material.button.MaterialButton;
import com.uithealthcare.domain.appointment.AppointmentInfo;
import com.uithealthcare.domain.appointment.AppointmentRequest;
import com.uithealthcare.domain.careProfile.CareProfile;
import com.uithealthcare.domain.careProfile.CareProfilesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private String TOKEN = null;

    private RecyclerView recyclerView;
    private MaterialButton btnBack;
    private MaterialButton btnCreateRecord;

    private List<ItemRecord> itemRecords;

    private AppointmentRequest req;

    private AppointmentInfo appointmentInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_activity);

        sp = getSharedPreferences("app_prefs", MODE_PRIVATE); // OK, context đã có
        TOKEN = sp.getString("access_token", null);

        req = new AppointmentRequest();
        appointmentInfo = new AppointmentInfo();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnCreateRecord = findViewById(R.id.btnCreateRecord);
//        btnCreateRecord.setOnClickListener(v ->{
//            Intent data = new Intent(this, CreateProfileActivity.class);
//            startActivity(data);
//        });

        itemRecords = new ArrayList<>();
        showOnCardRecord();
    }

    private void showOnCardRecord(){
        com.example.appointment.api.CareProfileService.CARE_PROFILE_API.showOnCardCareProfile(this.TOKEN)
                .enqueue(new Callback<CareProfilesResponse>() {
                    @Override
                    public void onResponse(Call<CareProfilesResponse> call, Response<CareProfilesResponse> response) {
                        CareProfilesResponse data = response.body();
                        if(data != null && data.isSuccess()){
                            List<CareProfile> list = data.getData();
                            for (CareProfile care : list){
                                itemRecords.add(new ItemRecord(care.getFullName(), care.getId(), care.getPhone()));
                            }
                        }
                        RecordAdapter adapter = new RecordAdapter(itemRecords);
                        recyclerView.setAdapter(adapter);

                        adapter.setOnItemClickListener(item -> {
                            Intent i = new Intent(AppointmentActivity.this, SpecialtyActivity.class);

                            req.setCareProfileId(item.getId());
                            appointmentInfo.setPatientName(item.getName());

                            i.putExtra(AppointmentRequest.EXTRA, req);
                            i.putExtra(AppointmentInfo.EXTRA, appointmentInfo);

                            Log.d("Req", "Đã có care profile ID: " + req.getCareProfileId());
                            startActivity(i);
                        });
                    }

                    @Override
                    public void onFailure(Call<CareProfilesResponse> call, Throwable throwable) {
                        Log.d("Card_Record", "showOnCardRecord failure");
                    }
                });
    }
}
