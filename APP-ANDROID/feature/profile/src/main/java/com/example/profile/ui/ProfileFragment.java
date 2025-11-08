package com.example.profile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.auth.ui.LoginActivity;
import com.example.profile.ui.EditProfileActivity;
import com.example.profile.R;
import com.uithealthcare.util.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        super(R.layout.profile); // layout profile.xml mà bạn đã có
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        // ====== Nút đăng xuất ======
        View rowLogout = v.findViewById(R.id.rowLogout);
        if (rowLogout != null) {
            rowLogout.setOnClickListener(view -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc muốn đăng xuất khỏi tài khoản?")
                        .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                        .setPositiveButton("Đăng xuất", (d, w) -> doLogout())
                        .show();
            });
        }

        // ====== Nút chỉnh sửa hồ sơ ======
        View rowEditProfile = v.findViewById(R.id.rowEditProfile);
        if (rowEditProfile != null) {
            rowEditProfile.setOnClickListener(view -> {
                Intent i = new Intent(requireContext(), EditProfileActivity.class);
                startActivity(i);
            });
        }
    }

    private void doLogout() {
        // Xoá token / dữ liệu đăng nhập
        new SessionManager(requireContext()).clear();

        // Quay về màn đăng nhập
        Intent i = new Intent(requireContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

        // Đảm bảo fragment hiện tại bị đóng
        requireActivity().finish();
    }
}
