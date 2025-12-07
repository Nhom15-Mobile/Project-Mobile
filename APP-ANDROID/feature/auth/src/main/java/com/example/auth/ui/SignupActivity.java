package com.example.auth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.auth.R;
import com.example.auth.data.AuthRepository;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPass, etConfirm;
    private MaterialButton btnSignup;
    private AuthRepository repo;
    private ImageView btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity); // nhớ tạo layout này trong feature/auth

        repo       = new AuthRepository(this);
        etName     = findViewById(R.id.editTextName);
        etEmail    = findViewById(R.id.editTextEmail);
        etPass     = findViewById(R.id.editTextPass);
        etConfirm  = findViewById(R.id.editTextPassConfirm);
        btnSignup  = findViewById(R.id.buttonSignup);
        btnBack = findViewById(R.id.btnBack);

        btnSignup.setOnClickListener(v -> doRegister());
        btnBack.setOnClickListener((v -> finish()));
    }

    private void doRegister() {
        String name  = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass  = etPass.getText().toString().trim();
        String rep   = etConfirm.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || rep.isEmpty()) {
            Toast.makeText(this, "Điền đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(rep)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSignup.setEnabled(false);

        // Mặc định role "PATIENT" (đổi nếu cần)
        repo.register(name, email, pass, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess(String message) {
                btnSignup.setEnabled(true);
                Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                // Quay về LoginActivity và prefill email
                try {
                    Class<?> login = Class.forName("com.example.auth.ui.LoginActivity");
                    Intent i = new Intent(SignupActivity.this, login);
                    i.putExtra("prefill_email", email);
                    startActivity(i);
                    finish();
                } catch (ClassNotFoundException e) {
                    finish();
                }
            }

            @Override
            public void onError(String message) {
                btnSignup.setEnabled(true);
                Toast.makeText(SignupActivity.this, "Đăng ký thất bại: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
