package com.example.mobile_app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    private ImageView imgMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_information); // layout của bạn

        imgMap = findViewById(R.id.imgMap);

        imgMap.setOnClickListener(v -> openMap());
    }

    private void openMap() {
        String address = "Hàn Thuyên, khu phố 6 P, Thủ Đức, Thành phố Hồ Chí Minh";
        String url = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(address);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        try {
            startActivity(intent);   // CỨ GỌI THẲNG
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Không mở được bản đồ", Toast.LENGTH_SHORT).show();
        }
    }

}
