package com.example.auth.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.example.auth.R;
import com.example.auth.data.AuthApi;
import com.example.auth.data.AuthRepository;

public class LoginActivity extends AppCompatActivity {

    private AuthRepository repo;
    private EditText etEmail, etPass;
    private MaterialButton btnLogin, btnGoSignup;
    private CheckBox cbSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Ánh xạ view
        repo        = new AuthRepository(this);
        etEmail     = findViewById(R.id.editTextEmail);
        etPass      = findViewById(R.id.editTextPass);
        btnLogin    = findViewById(R.id.buttonLogin);
        btnGoSignup = findViewById(R.id.buttonsignup);
        cbSave      = findViewById(R.id.checkBoxSaveInfor);

        // Load email nếu có lưu
        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String savedEmail = sp.getString("saved_email", "");
        if (!savedEmail.isEmpty()) etEmail.setText(savedEmail);

        // Xử lý đăng nhập
        btnLogin.setOnClickListener(v -> doLogin());

        // Chuyển sang SignupActivity
        btnGoSignup.setOnClickListener(v -> {
            try {
                Class<?> signup = Class.forName("com.example.auth.ui.SignupActivity");
                startActivity(new Intent(LoginActivity.this, signup));
            } catch (ClassNotFoundException ignored) {}
        });

        // Nhận email điền sẵn khi quay về từ đăng ký
        String prefill = getIntent().getStringExtra("prefill_email");
        if (prefill != null) etEmail.setText(prefill);
    }

    private void doLogin() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPass.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Nhập email & mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        repo.login(email, pass, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(AuthApi.LoginResp.Data data) {
                btnLogin.setEnabled(true);

                // Lưu token (nếu có)
                String token = null;
                if (data != null) {
                    if (data.accessToken != null && !data.accessToken.isEmpty()) token = data.accessToken;
                    else if (getFieldSafely(data, "token") != null) token = getFieldSafely(data, "token");
                }

                if (token != null) {
                    SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    sp.edit().putString("access_token", token).apply();
                }

                // Lưu hoặc xóa email
                SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                if (cbSave.isChecked()) {
                    sp.edit().putString("saved_email", email).apply();
                } else {
                    sp.edit().remove("saved_email").apply();
                }

                // Đăng nhập thành công → sang HomeActivity
                try {
                    Class<?> homeClass = Class.forName("com.example.mobile_app.HomeActivity");
                    Intent intent = new Intent(LoginActivity.this, homeClass);
                    startActivity(intent);
                    finish();
                } catch (ClassNotFoundException e) {
                    Toast.makeText(LoginActivity.this, "Không tìm thấy HomeActivity", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String errorMessage) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Lấy giá trị field token nếu tên khác accessToken
    private String getFieldSafely(AuthApi.LoginResp.Data data, String fieldName) {
        try {
            java.lang.reflect.Field f = AuthApi.LoginResp.Data.class.getDeclaredField(fieldName);
            f.setAccessible(true);
            Object v = f.get(data);
            return v instanceof String ? (String) v : null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
