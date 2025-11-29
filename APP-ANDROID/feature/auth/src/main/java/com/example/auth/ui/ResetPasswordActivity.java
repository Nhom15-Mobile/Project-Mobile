package com.example.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.auth.R;
import com.example.auth.data.AuthRepository;
import com.google.android.material.button.MaterialButton;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNewPassword;
    private EditText etConfirmPassword;
    private MaterialButton btnResetPassword;

    private AuthRepository repo;

    // nhận từ màn ForgotPasswordActivity
    private String email;
    private String code; // OTP / reset code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d("RESET_PASS", "email=" + email + ", code=" + code);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        // khởi tạo repo
        repo = new AuthRepository(this);

        // nhận email + code otp từ Intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        code  = intent.getStringExtra("code");

        // ánh xạ view
        etNewPassword     = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword  = findViewById(R.id.buttonConfirm);

        btnResetPassword.setOnClickListener(v -> {
            String newPass = etNewPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
//            if (newPass.length() < 6) {
//                Toast.makeText(this, "Mật khẩu phải ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
//                return;
//            }

            // API reset password
            repo.resetPassword(email, code, newPass, new AuthRepository.ResetPasswordCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    // đổi mật khẩu thành công → quay về màn login
                    Intent i = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    // xoá history để bấm back không quay lại màn reset
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
