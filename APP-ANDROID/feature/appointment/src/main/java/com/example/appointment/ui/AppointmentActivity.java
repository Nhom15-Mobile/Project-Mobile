package com.example.appointment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.api.CareProfileService;
import com.example.appointment.model.ItemRecord;
import com.example.appointment.adapter.RecordAdapter;
import com.google.android.material.button.MaterialButton;
import com.uithealthcare.domain.appointment.AppointmentInfo;
import com.uithealthcare.domain.appointment.AppointmentRequest;
import com.uithealthcare.domain.careProfile.CareProfile;
import com.uithealthcare.domain.careProfile.CareProfilesResponse;
import com.uithealthcare.network.ApiServices;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MaterialButton btnBack, btnCreateRecord;
    private List<ItemRecord> itemRecords;

    private AppointmentRequest req;

    private AppointmentInfo appointmentInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_activity);
        initView();
        initEvent();

        SessionManager manager = new SessionManager(this);

        SessionInterceptor.TokenProvider tokenProvider = new SessionInterceptor.TokenProvider() {
            @Override
            public String getToken() {
                return manager.getBearer();
            }
        };

        CareProfileService careProfileService = ApiServices.create(CareProfileService.class, tokenProvider);


        showOnCardRecord(careProfileService);
    }

    private void initView(){
        req = new AppointmentRequest();
        appointmentInfo = new AppointmentInfo();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);
        btnCreateRecord = findViewById(R.id.btnCreateRecord);

        itemRecords = new ArrayList<>();
    }

    private void initEvent(){
        btnBack.setOnClickListener(v -> finish());

        btnCreateRecord.setOnClickListener(v -> {
            startActivity(new Intent(AppointmentActivity.this, CreateProfileActivity.class));
        });
    }

    private void showOnCardRecord(CareProfileService careProfileService){
        careProfileService.showOnCardCareProfile()
                .enqueue(new Callback<CareProfilesResponse>() {
                    @Override
                    public void onResponse(Call<CareProfilesResponse> call, Response<CareProfilesResponse> response) {
                        CareProfilesResponse data = response.body();
                        if(data != null && data.isSuccess()){
                            List<CareProfile> list = data.getData();
                            for (CareProfile care : list){
                                itemRecords.add(new ItemRecord(care.getFullName(), care.getId(), care.getPhone()));
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
                    }

                    @Override
                    public void onFailure(Call<CareProfilesResponse> call, Throwable throwable) {
                        Log.d("Card_Record", "showOnCardRecord failure");
                    }
                });
    }
}
