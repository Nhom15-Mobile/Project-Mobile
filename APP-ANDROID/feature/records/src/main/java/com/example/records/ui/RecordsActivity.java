package com.example.records.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.records.R;
import com.example.records.adapter.RecordsAdapter;
import com.example.records.model.Record;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class RecordsActivity extends AppCompatActivity implements RecordsAdapter.RecordListener {
    RecyclerView rcv;
    RecordsAdapter adapter;
    List<Record> listRecord;
    MaterialButton btnBack;

    String name;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_activity);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        displayRecord();
    }

    private void displayRecord(){
        rcv = findViewById(R.id.recyclerView);
        rcv.setLayoutManager(new LinearLayoutManager(this));

        listRecord = new ArrayList<>();
        listRecord.add(new Record("Nguyễn Văn A", "12345678", "01234829321"));
        listRecord.add(new Record("Nguyễn Văn B", "12345678", "01234829321"));
        listRecord.add(new Record("Nguyễn Văn C", "12345678", "01234829321"));
        listRecord.add(new Record("Nguyễn Văn D", "12345678", "01234829321"));
        listRecord.add(new Record("Nguyễn Văn E", "12345678", "01234829321"));
        listRecord.add(new Record("Nguyễn Văn F", "12345678", "01234829321"));
        listRecord.add(new Record("Nguyễn Văn G", "12345678", "01234829321"));

        adapter = new RecordsAdapter(listRecord, this);
        rcv.setAdapter(adapter);
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

    @Override
    public void onRecordClicked(Record record) {
        name = record.getName();
        showProfileActionBottomSheet();
    }
}
