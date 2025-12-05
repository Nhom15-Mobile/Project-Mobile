package com.uithealthcare.util;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

public class HandleImage {
    static public void  openGallery(ActivityResultLauncher<Intent> pickImageLauncher) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}
