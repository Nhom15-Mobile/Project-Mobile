package com.example.records.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.records.ui.AppointmentHistoryListFragment;

public class AppointmentHistoryPagerAdapter extends FragmentStateAdapter {
    private String careProfileId;
    // truyền tiếp tới listfragment
    public AppointmentHistoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, String careProfileId) {
        super(fragmentActivity);
        this.careProfileId = careProfileId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return AppointmentHistoryListFragment.newInstance("UPCOMING",careProfileId);  // Chưa khám
            case 1:
                return AppointmentHistoryListFragment.newInstance("DONE",careProfileId);      // Đã khám
//            default:
//                return AppointmentHistoryListFragment.newInstance("CANCELED");  // Đã huỷ
            default:
                throw new IllegalStateException("Invalid position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
