package com.example.auth.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.auth.R;
import com.example.auth.data.AuthApi;
import com.example.auth.data.AuthRepository;
import com.uithealthcare.util.SessionManager;
import com.uithealthcare.network.RetrofitProvider;
import com.uithealthcare.network.SessionInterceptor;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private AuthRepository repo;
    private EditText etEmail, etPass;
    private MaterialButton btnLogin, btnGoSignup;
    private CheckBox cbSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        repo        = new AuthRepository(this);
        etEmail     = findViewById(R.id.editTextEmail);
        etPass      = findViewById(R.id.editTextPass);
        btnLogin    = findViewById(R.id.buttonLogin);
        btnGoSignup = findViewById(R.id.buttonsignup);
        cbSave      = findViewById(R.id.checkBoxSaveInfor);

        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String savedEmail = sp.getString("saved_email", "");
        if (!savedEmail.isEmpty()) etEmail.setText(savedEmail);

        btnLogin.setOnClickListener(v -> doLogin());

        btnGoSignup.setOnClickListener(v -> {
            try {
                Class<?> signup = Class.forName("com.example.auth.ui.SignupActivity");
                startActivity(new Intent(LoginActivity.this, signup));
            } catch (ClassNotFoundException ignored) {}
        });

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
        android.util.Log.d("AUTH", "Đang gọi login API với email=" + email);

        repo.login(email, pass, new AuthRepository.LoginCallback() {
            @Override
            public void onSuccess(AuthApi.LoginResp.Data data) {
                btnLogin.setEnabled(true);
                android.util.Log.d("AUTH", "onSuccess() được gọi, data=" + (data == null ? "null" : "non-null"));
                String token = null;
                if (data != null) {
                    android.util.Log.d("AUTH", "LoginResp.Data → accessToken=" + data.accessToken + ", email=" + data.email);
                    if (data.accessToken != null && !data.accessToken.isEmpty()) {
                        token = data.accessToken;
                    } else {
                        String alt = getFieldSafely(data, "token");
                        if (alt != null && !alt.isEmpty()) token = alt;

                        String snake = getFieldSafely(data, "access_token");
                        if (token == null && snake != null && !snake.isEmpty()) token = snake;
                    }
                }

                if (token == null || token.isEmpty()) {
                    android.util.Log.w("AUTH", "⚠️ token == null từ model → thử fallback loginRaw() đọc JSON gốc");

                    // Tạo AuthApi không có Bearer để gọi loginRaw
                    SessionInterceptor.TokenProvider p = new SessionInterceptor.TokenProvider() {
                        @Override public String getToken() { return null; }
                    };
                    AuthApi api = RetrofitProvider.get(p).create(AuthApi.class);

                    api.loginRaw(new AuthApi.LoginReq(
                            etEmail.getText().toString().trim(),
                            etPass.getText().toString().trim()
                    )).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> resp) {
                            if (!resp.isSuccessful() || resp.body() == null) {
                                Toast.makeText(LoginActivity.this, "Login ok nhưng không đọc được token (raw)", Toast.LENGTH_LONG).show();
                                return;
                            }
                            JsonObject json = resp.body();
                            android.util.Log.d("AUTH", "loginRaw body=" + json.toString());

                            String tok = extractTokenFromJson(json);
                            if (tok == null || tok.isEmpty()) {
                                Toast.makeText(LoginActivity.this, "Login thành công nhưng JSON không có token!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            afterGotToken(tok);
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Login raw fail: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    return; // chờ fallback xử lý
                }

                // Có token ngay trong model
                afterGotToken(token);
            }

            @Override
            public void onError(String errorMessage) {
                btnLogin.setEnabled(true);
                android.util.Log.e("AUTH", "Login lỗi: " + errorMessage);
                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    // === Helpers ===

    // Lưu token vào prefs + SessionManager rồi điều hướng
    private void afterGotToken(String token) {
        // (A) Lưu vào app_prefs (tuỳ bạn)
        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        sp.edit().putString("access_token", "Bearer "+token).apply();

        // (B) Lưu cho network layer (SessionInterceptor sẽ đọc "Bearer ...")
        SessionManager sm = new SessionManager(LoginActivity.this);
        sm.saveBearer(token);
        android.util.Log.d("AUTH", "Saved bearer (login): " + sm.getBearer());
        com.uithealthcare.network.RetrofitProvider.reset();
        // (C) Lưu email nếu cần
        if (cbSave.isChecked()) {
            sp.edit().putString("saved_email", etEmail.getText().toString().trim()).apply();
        } else {
            sp.edit().remove("saved_email").apply();
        }

        // (D) Điều hướng
        try {
            Class<?> home = Class.forName("com.example.mobile_app.HomeActivity");
            Intent i = new Intent(LoginActivity.this, home);
            // tuỳ bạn: nếu muốn xoá hẳn Login khỏi back stack:
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish(); // chặn back về Login
        } catch (ClassNotFoundException e) {
            Toast.makeText(LoginActivity.this, "Không tìm thấy HomeActivity", Toast.LENGTH_LONG).show();
        }
    }

    // Bắt token ở mọi vị trí có thể trong JSON
    private String extractTokenFromJson(JsonObject json) {
        // Root keys
        String[] keys = {"accessToken", "token", "access_token", "jwt", "idToken"};
        for (String k : keys) {
            if (json.has(k) && json.get(k).isJsonPrimitive()) {
                String v = json.get(k).getAsString();
                if (v != null && !v.isEmpty()) return v;
            }
        }
        // data.*
        if (json.has("data") && json.get("data").isJsonObject()) {
            JsonObject d = json.getAsJsonObject("data");
            for (String k : keys) {
                if (d.has(k) && d.get(k).isJsonPrimitive()) {
                    String v = d.get(k).getAsString();
                    if (v != null && !v.isEmpty()) return v;
                }
            }
        }
        return null;
    }

    // Lấy giá trị field token nếu tên khác accessToken (trong model Data)
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
