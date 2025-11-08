package com.example.profile;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.profile.R;

public class EditProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile); // dùng layout edit_profile của bạn

        // nút back (nếu trong edit_profile có ImageView id=btnBack)
        View back = findViewById(R.id.btnBack);
        if (back != null) back.setOnClickListener(v -> onBackPressed());
    }
}
