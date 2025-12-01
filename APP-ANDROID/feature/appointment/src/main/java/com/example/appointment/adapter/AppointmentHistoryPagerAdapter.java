package com.example.appointment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.appointment.ui.AppointmentHistoryListFragment;

public class AppointmentHistoryPagerAdapter extends FragmentStateAdapter {

    public AppointmentHistoryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return AppointmentHistoryListFragment.newInstance("UPCOMING");  // Chưa khám
            case 1:
                return AppointmentHistoryListFragment.newInstance("DONE");      // Đã khám
            default:
                return AppointmentHistoryListFragment.newInstance("CANCELED");  // Đã huỷ
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
