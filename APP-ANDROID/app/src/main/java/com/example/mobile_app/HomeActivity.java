package com.example.mobile_app;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.profile.ui.ProfileFragment;
import com.example.notification.ui.NotificationFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private Fragment homeFrag;
    private Fragment profileFrag;
    private Fragment activeFrag;
    private Fragment supportFrag;

    private Fragment notifiFrag;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity); // layout chứa container + bottom bar

        BottomNavigationView bottom = findViewById(R.id.bottom_nav);

        if (savedInstanceState == null) {
            // tạo & add 3 fragment, ẩn Profile để giữ state
            homeFrag = new HomeFragment();
            profileFrag = new ProfileFragment();
            supportFrag = new SupportFragment();
            notifiFrag = new NotificationFragment();




            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nav_host_container, profileFrag, "PROFILE").hide(profileFrag)
                    .add(R.id.nav_host_container, homeFrag, "HOME")
                    .add(R.id.nav_host_container, notifiFrag, "NOTIFICATION").hide(notifiFrag)
                    .add(R.id.nav_host_container, supportFrag, "SUPPORT").hide(supportFrag)
                    .commit();

            activeFrag = homeFrag;
        } else {
            // lấy lại từ FragmentManager (sau khi xoay màn…)
            homeFrag = getSupportFragmentManager().findFragmentByTag("HOME");
            profileFrag = getSupportFragmentManager().findFragmentByTag("PROFILE");
            supportFrag = getSupportFragmentManager().findFragmentByTag("SUPPORT");
            notifiFrag = getSupportFragmentManager().findFragmentByTag("NOTIFICATION");
            activeFrag = (getSupportFragmentManager().findFragmentById(R.id.nav_host_container));
        }

        bottom.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            android.util.Log.d("BottomNav", "Clicked item id: " + getResources().getResourceEntryName(id));
            if (id == R.id.nav_home) {
                switchTo(homeFrag);
                return true;
            } else if (id == R.id.nav_profile) {
                switchTo(profileFrag);
                return true;
            } else if (id == R.id.nav_support) {
                switchTo(supportFrag);
                return true;
            } else if (id == R.id.nav_notify) {
                switchTo(notifiFrag);
                return true;
            }
            return false;
        });

        bottom.setOnItemReselectedListener(i -> { /* tránh reload tab */ });
    }

    private void switchTo(Fragment target) {
        if (target == null || target == activeFrag) return;
        android.util.Log.d("BottomNav", "Switching to: " + target.getTag());

        getSupportFragmentManager().beginTransaction()
                .hide(activeFrag)
                .show(target)
                .commit();
        activeFrag = target;
    }
}
