package com.example.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.uithealthcare.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.content.Intent;
import com.example.profile.R;

// Nếu bạn có Room DB, import AppDatabase và clearAllTables() trong logout()

public class ProfileFragment extends Fragment {

    public ProfileFragment() { super(R.layout.profile); }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        View rowLogout = v.findViewById(R.id.rowLogout); // ĐẶT id này trong layout của bạn
        rowLogout.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất khỏi tài khoản?")
                    .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                    .setPositiveButton("Đăng xuất", (d, w) -> doLogout())
                    .show();
        });
    }

    private void doLogout() {
        new SessionManager(requireContext()).clear();

        Intent i = new Intent(requireContext(),
                com.example.auth.ui.LoginActivity.class); // Đúng package/class thật của bạn
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }



}
