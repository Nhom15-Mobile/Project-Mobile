package com.example.records.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.records.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvDob, tvGender, tvPhone, tvRelation,
            tvProvince, tvDistrict, tvWard, tvAddressDetail;
    private ImageView btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        initView();
        bindDataFromIntent();
        initEvent();
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvName);
        tvDob = findViewById(R.id.tvDob);
        tvGender = findViewById(R.id.tvGender);
        tvPhone = findViewById(R.id.tvPhone);
        tvRelation = findViewById(R.id.tvRelation);
        tvProvince = findViewById(R.id.tvProvince);
        tvDistrict = findViewById(R.id.tvDistrict);
        tvWard = findViewById(R.id.tvWard);
        tvAddressDetail = findViewById(R.id.tvAddressDetail);

    }

    private void bindDataFromIntent() {

        String name = getIntent().getStringExtra("name");
        String dob = getIntent().getStringExtra("dob");
        String gender = getIntent().getStringExtra("gender");
        String phone = getIntent().getStringExtra("phone");
        String relation = getIntent().getStringExtra("relation");
        String province = getIntent().getStringExtra("province");
        String district = getIntent().getStringExtra("district");
        String ward = getIntent().getStringExtra("ward");
        String addressDetail = getIntent().getStringExtra("addressDetail");

        // Gán vào TextView nếu có dữ liệu
        if (name != null) tvName.setText(name);
        if (dob != null) tvDob.setText(dob);


        if (gender != null) tvGender.setText(gender);
        if (phone != null) tvPhone.setText(phone);
        if (relation != null) tvRelation.setText(relation);

        if (province != null) tvProvince.setText(province);
        if (district != null) tvDistrict.setText(district);
        if (ward != null) tvWard.setText(ward);
        if (addressDetail != null) {
            tvAddressDetail.setText(addressDetail);
        }
    }


    private void initEvent() {
        btnBack.setOnClickListener(v -> finish());
    }
}
