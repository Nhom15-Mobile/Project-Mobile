package com.uithealthcare.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class ScanManager {
    public static Uri cameraImageUri;
    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    static public void  openGallery(ActivityResultLauncher<Intent> pickImageLauncher) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    public static void openCamera(View view,
                                  ActivityResultLauncher<Uri> takePictureLauncher) {

        Context context = view.getContext();

        // 1. Kiểm tra quyền CAMERA
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Xin quyền (cũ nhưng đơn giản)
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );

            // Xin xong sẽ quay lại Activity.onRequestPermissionsResult
            // Người dùng bấm lại nút "Quét" lần nữa là được
            return;
        }

        // 2. ĐÃ có quyền → tạo file & mở camera
        try {
            File photoFile = File.createTempFile(
                    "cccd_",   // prefix
                    ".jpg",    // suffix
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );

            cameraImageUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    photoFile
            );

            takePictureLauncher.launch(cameraImageUri);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Không tạo được file ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    static public void showScanOptionDialog(View view,
                                            ActivityResultLauncher<Intent> pickImageLauncher,
                                            ActivityResultLauncher<Uri> takePictureLauncher) {
        String[] options = {"Chụp bằng camera", "Chọn từ thư viện"};

        new AlertDialog.Builder(view.getContext())
                .setTitle("Chọn cách quét CCCD")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera(view, takePictureLauncher);
                    } else {
                        openGallery(pickImageLauncher);
                    }
                })
                .show();
    }
}
