package com.example.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.auth.R;
import com.example.auth.data.AuthRepository;
import com.example.auth.ui.dialog.OtpDialog;
import com.google.android.material.button.MaterialButton;

public class ForgotPasswordActivity extends AppCompatActivity {

    private MaterialButton buttonLogin;
    private AuthRepository repo;
    private EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_otp_to_reset_pass); // layout bạn gửi ở trên

        repo = new AuthRepository(this);

        buttonLogin = findViewById(R.id.buttonLogin);
        editTextEmail = findViewById(R.id.editTextEmail);

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }
            repo.forgotPassword(email, new AuthRepository.ForgotPasswordCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                    // API gửi mã thành công → mở dialog OTP
                    showOtpDialog(email);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showOtpDialog(String email) {
        OtpDialog dialog = new OtpDialog(new OtpDialog.OtpCallback() {
            @Override
            public void onOtpSubmit(String otp) {

                // TODO: gọi API verify OTP + chuyển màn reset mật khẩu
                // Ví dụ: startActivity(new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class));
                Intent i = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                i.putExtra("email", email);
                i.putExtra("code", otp);
                startActivity(i);
            }

            @Override
            public void onResendClicked() {
                // TODO: gọi API resend OTP
            }
        });

        dialog.show(getSupportFragmentManager(), "OtpDialog");
    }

}
