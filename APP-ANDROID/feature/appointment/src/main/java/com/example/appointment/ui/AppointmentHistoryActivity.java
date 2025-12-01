package com.example.appointment.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.appointment.R;
import com.example.appointment.adapter.AppointmentHistoryPagerAdapter;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AppointmentHistoryActivity extends AppCompatActivity {

    private TabLayout tabHistory;
    private ViewPager2 viewPagerHistory;
    private AppointmentHistoryPagerAdapter pagerAdapter;

    private MaterialButton btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_his);

//        MaterialToolbar toolbar = findViewById(R.id.toolbarHistory);
//        setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        tabHistory = findViewById(R.id.tabHistory);
        viewPagerHistory = findViewById(R.id.viewPagerHistory);

        pagerAdapter = new AppointmentHistoryPagerAdapter(this);
        viewPagerHistory.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabHistory, viewPagerHistory,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Chưa khám");
                            break;
                        case 1:
                            tab.setText("Đã khám");
                            break;
                        case 2:
                            tab.setText("Đã huỷ");
                            break;
                    }
                }).attach();
    }
}
