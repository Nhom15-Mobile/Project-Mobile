package com.example.records.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.records.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class ChooseResultActivity extends AppCompatActivity {
    MaterialAutoCompleteTextView spinnerYear, spinnerMonth;
    MaterialButton btnClear, btnBack;
    TextView tv_name;

    MaterialCardView cardResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_result_activity);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tv_name = findViewById(R.id.tv_name);
        Intent data = getIntent();
        String name = data.getStringExtra("labelName");
        tv_name.setText(name);

        cardResult = findViewById(R.id.cardResult);
        cardResult.setOnClickListener(v -> onResultClick());

        initFilter();
    }

    private void initFilter(){
        spinnerYear = findViewById(R.id.spinnerYear);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        btnClear = findViewById(R.id.btnClear);

        // Dữ liệu Năm
        List<String> years = new ArrayList<>();
        for (int y = 1990; y <= 2025; y++) years.add(String.valueOf(y));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, years);
        spinnerYear.setAdapter(yearAdapter);
        // bấm vào là mở list

        spinnerYear.setOnClickListener(v -> spinnerYear.showDropDown());
        spinnerYear.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) spinnerYear.showDropDown();
        });

        // Dữ liệu Tháng
        String[] months = {"1","2","3","4","5","6","7","8","9","10","11","12"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, months);
        spinnerMonth.setAdapter(monthAdapter);

        spinnerMonth.setOnClickListener(v -> spinnerMonth.showDropDown());
        spinnerMonth.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) spinnerMonth.showDropDown();
        });

        // Nút X - clear toàn bộ
        btnClear.setOnClickListener(v -> {
            spinnerYear.setText(null, false);
            spinnerMonth.setText(null, false);
        });
    }

    private void onResultClick(){

    }
}
