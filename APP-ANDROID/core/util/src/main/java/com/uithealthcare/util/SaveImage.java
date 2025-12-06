package com.uithealthcare.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class SaveImage {

    // 1. Chụp 1 View thành Bitmap
    public static Bitmap captureViewToBitmap(android.view.View view) {
        // Đảm bảo view đã đo & layout
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(
                view.getWidth(),
                view.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    // 2. Lưu Bitmap xuống thư viện máy (Gallery)
    public static void saveBitmapToGallery(Context context, Bitmap bitmap, String fileName) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ dùng MediaStore với RELATIVE_PATH
                ContentResolver resolver = context.getContentResolver();
                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                values.put(MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/UITHealthcare");

                Uri imageUri = resolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                );

                if (imageUri == null) {
                    Toast.makeText(context, "Không thể tạo Uri để lưu ảnh", Toast.LENGTH_SHORT).show();
                    return;
                }

                fos = resolver.openOutputStream(imageUri);

            } else {
                // Android 9 trở xuống: lưu ra thư mục public Pictures
                String imagesDir = Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString() + File.separator + "UITHealthcare";

                File dir = new File(imagesDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File image = new File(dir, fileName);
                fos = new FileOutputStream(image);

            }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }

            Toast.makeText(context, "Đã lưu phiếu khám vào thư viện", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
