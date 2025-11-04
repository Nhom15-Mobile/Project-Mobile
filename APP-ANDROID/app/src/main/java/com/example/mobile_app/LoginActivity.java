package com.example.mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import com.example.mobile_app.HomeActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        MaterialButton btnLogin = findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Nếu đăng nhập thành công → chuyển màn hình
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);

                // (Tuỳ chọn) Kết thúc LoginActivity nếu không muốn quay lại
                finish();
            }
        });
    }
}
