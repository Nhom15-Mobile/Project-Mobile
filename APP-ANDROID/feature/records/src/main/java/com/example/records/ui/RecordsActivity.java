package com.example.records.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.records.R;
import com.example.records.adapter.RecordAdapter;
import com.example.records.api.CareProfileService;
import com.example.records.model.ItemRecord;
import com.example.records.model.Record;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.uithealthcare.domain.careProfile.CareProfile;
import com.uithealthcare.domain.careProfile.CareProfilesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordsActivity extends AppCompatActivity {
    RecyclerView rcv;
    private SharedPreferences sp;
    private String TOKEN = null;
    MaterialButton btnBack;
    private List<ItemRecord> itemRecords = new ArrayList<>();
    String name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_activity);

        sp = getSharedPreferences("app_prefs", MODE_PRIVATE); // OK, context đã có
        TOKEN = sp.getString("access_token", null);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rcv = findViewById(R.id.recyclerView);
        rcv.setLayoutManager(new LinearLayoutManager(this));

        // Gọi API   + appdater
        showOnCardRecord();

    }

    private void showOnCardRecord() {
//        if (TOKEN == null) {
//            Toast.makeText(this, "Không tìm thấy token, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
//            return;
//        }

        CareProfileService.CARE_PROFILE_API
                .showOnCardCareProfile(this.TOKEN)   // nếu backend yêu cầu "Bearer " thì sửa thành "Bearer " + TOKEN
                .enqueue(new Callback<CareProfilesResponse>() {
                    @Override
                    public void onResponse(Call<CareProfilesResponse> call, Response<CareProfilesResponse> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Log.d("RecordsActivity", "API error: " + response.code());
                            Toast.makeText(RecordsActivity.this,
                                    "Không tải được danh sách hồ sơ (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CareProfilesResponse data = response.body();
                        if (data != null && data.isSuccess()) {
                            List<CareProfile> list = data.getData();
                            itemRecords.clear();
                            if (list != null) {
                                for (CareProfile care : list) {
                                    // chỉnh getter cho đúng với CareProfile của bạn
                                    itemRecords.add(new ItemRecord(
                                            care.getFullName(),
                                            genCareId(care.getId()),
                                            care.getPhone(),
                                            care.getRelation()
                                    ));
                                }
                            }
                        }

                        RecordAdapter adapter = new RecordAdapter(itemRecords);
                        rcv.setAdapter(adapter);
                        adapter.setOnItemClickListener(item -> {
                            name = item.getName();
                            showProfileActionBottomSheet();
                        });
                    }

                    @Override
                    public void onFailure(Call<CareProfilesResponse> call, Throwable throwable) {
                        Log.d("RecordsActivity", "showOnCardRecord failure: " + throwable.getMessage());
                        Toast.makeText(RecordsActivity.this,
                                "Lỗi kết nối máy chủ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProfileActionBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_profile_actions, null);
        bottomSheetDialog.setContentView(view);

        MaterialButton btnViewInfo = view.findViewById(R.id.btnViewInfo);
        MaterialButton btnViewResult = view.findViewById(R.id.btnViewResult);
        MaterialButton btnViewHistory = view.findViewById(R.id.btnViewHistory);
        MaterialButton btnClose = view.findViewById(R.id.btnClose);

        btnViewInfo.setOnClickListener(v -> {
            Intent data = new Intent(this, ProfileActivity.class);
            data.putExtra("labelName", name);
            startActivity(data);
            bottomSheetDialog.dismiss();
        });

        btnViewResult.setOnClickListener(v -> {
            Intent data = new Intent(this, ChooseResultActivity.class);
            data.putExtra("labelName", name);
            startActivity(data);
            // TODO: mở màn hình xem kết quả cận lâm sàng
            bottomSheetDialog.dismiss();
        });

        btnViewHistory.setOnClickListener(v -> {
            // TODO: mở màn hình xem lịch sử đặt khám
            bottomSheetDialog.dismiss();
        });

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private String genCareId(String careprofileId) {
        // Lấy 4 ký tự cuối từ appointmentId (nếu dài)
        String tail = careprofileId;
        if (careprofileId != null && careprofileId.length() > 4) {
            tail = careprofileId.substring(careprofileId.length() - 4);
        }
        return "HS" + "_" + tail.toUpperCase();
    }


}
