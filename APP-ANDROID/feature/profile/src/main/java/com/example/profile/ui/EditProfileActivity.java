package com.example.profile.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.profile.R;
import com.example.profile.data.PatientApi;
import com.uithealthcare.network.RetrofitProvider;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.SessionManager;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnBack;

    // READ-ONLY
    private TextView tvFullName, tvEmail;

    // Editable
    private EditText etGender, etDob, etPhone, etAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Ánh xạ view
        imgAvatar  = findViewById(R.id.imgAvatar);
        btnBack    = findViewById(R.id.btnBack);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail    = findViewById(R.id.tvEmail);
        etGender   = findViewById(R.id.etGender);
        etDob      = findViewById(R.id.etDob);
        etPhone    = findViewById(R.id.etPhone);
        etAddress  = findViewById(R.id.etAddress);

        if (btnBack != null) btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Khóa bàn phím cho Gender & Dob (chỉ chọn từ menu/lịch)
        etGender.setFocusable(false);
        etGender.setClickable(true);
        etGender.setInputType(InputType.TYPE_NULL);

        etDob.setFocusable(false);
        etDob.setClickable(true);
        etDob.setInputType(InputType.TYPE_NULL);

        // Pickers
        setupPickers();

        // Khôi phục token nếu cần
        ensureToken();

        // Tải hồ sơ hiện tại
        loadProfile();

        // Lưu thay đổi
        findViewById(R.id.btnSave).setOnClickListener(v -> saveProfile());
    }

    private void ensureToken() {
        SessionManager sm = new SessionManager(this);
        String bearer = sm.getBearer();
        if (bearer == null) {
            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String raw = sp.getString("access_token", null);
            if (!TextUtils.isEmpty(raw)) {
                sm.saveBearer(raw);
            }
        }
    }

    private PatientApi api() {
        SessionInterceptor.TokenProvider provider =
                () -> new SessionManager(EditProfileActivity.this).getBearer();
        return RetrofitProvider.get(provider).create(PatientApi.class);
    }

    // ============== LOAD PROFILE ==============
    private void loadProfile() {
        api().getMyProfile().enqueue(new Callback<PatientApi.GetProfileResp>() {
            @Override
            public void onResponse(Call<PatientApi.GetProfileResp> call, Response<PatientApi.GetProfileResp> resp) {
                if (!resp.isSuccessful() || resp.body() == null || resp.body().data == null) return;
                bind(resp.body().data);
            }

            @Override
            public void onFailure(Call<PatientApi.GetProfileResp> call, Throwable t) {
                // optional: Toast/log
            }
        });
    }

//    private void bind(PatientApi.ProfileData d) {
//        // READ-ONLY từ data.user.*
//        if (d.user != null) {
//            tvFullName.setText(nz(d.user.fullName));
//            tvEmail.setText(nz(d.user.email));
//            // phone nếu backend để ở user.phone thì ưu tiên hiển thị
//            if (!TextUtils.isEmpty(d.emergencyContact)) {
//                etPhone.setText(d.emergencyContact);
//            }
//        }
//
//        // Editable từ data.*
//        etGender.setText(mapGenderToDisplay(d.gender));      // "male" -> "Nam"
//        etDob.setText(formatDobIsoZToDisplay(d.dob));        // ISO -> dd/MM/yyyy
//        if (!TextUtils.isEmpty(d.address)) etAddress.setText(d.address);
//        // Nếu có thêm insurance/emergency trên UI thì set luôn ở đây
//    }
    private void bind(PatientApi.ProfileData d) {
        if (d.user != null) {
            tvFullName.setText(nz(d.user.fullName));
            tvEmail.setText(nz(d.user.email));
        }

        // Hiển thị SĐT từ emergencyContact
        etPhone.setText(nz(d.emergencyContact));   // ← quan trọng

        etGender.setText(mapGenderToDisplay(d.gender));
        etDob.setText(formatDobIsoZToDisplay(d.dob));
        if (!TextUtils.isEmpty(d.address)) etAddress.setText(d.address);
    }


    // ============== SAVE PROFILE ==============
    private void saveProfile() {
        String genderApi = mapGenderToApi(trim(etGender.getText().toString())); // male/female/other
        String dobIso    = toIsoDate(trim(etDob.getText().toString()));         // yyyy-MM-dd
        String emergency     = trim(etPhone.getText().toString());
        String address   = trim(etAddress.getText().toString());


        // Body cơ bản (đúng key theo PatientApi.UpdateReq mới)
        PatientApi.UpdateReq body = new PatientApi.UpdateReq(
                emptyToNull(genderApi),   // gender
                emptyToNull(dobIso),      // dob (yyyy-MM-dd)
                emptyToNull(address),     // address
                null,                     // insuranceNumber (không cập nhật)
                emptyToNull(emergency),   // emergencyContact  ← quan trọng
                null,                     // phone (không gửi)
                null,                     // fullName (read-only)
                null                      // email   (read-only)
        );

        // Chặn double click
        findViewById(R.id.btnSave).setEnabled(false);

        api().updateMyProfile(body).enqueue(new Callback<PatientApi.UpdateResp>() {
            @Override
            public void onResponse(Call<PatientApi.UpdateResp> call, Response<PatientApi.UpdateResp> resp) {
                findViewById(R.id.btnSave).setEnabled(true);

                if (!resp.isSuccessful() || resp.body() == null) {
                    toast("Cập nhật thất bại");
                    return;
                }

                if (resp.body().data != null) {
                    bind(resp.body().data); // bind trực tiếp nếu server trả lại data
                } else {
                    loadProfile();          // nếu không có data thì reload từ server
                }

                toast("Đã lưu thay đổi");
            }

            @Override
            public void onFailure(Call<PatientApi.UpdateResp> call, Throwable t) {
                findViewById(R.id.btnSave).setEnabled(true);
                toast("Không thể kết nối máy chủ");
            }
        });
    }

    // ============== Pickers ==============
    private void setupPickers() {
        // Giới tính: PopupMenu chọn Nam/Nữ/Khác
        etGender.setOnClickListener(v -> {
            android.widget.PopupMenu menu = new android.widget.PopupMenu(this, v);
            menu.getMenu().add("Nam");
            menu.getMenu().add("Nữ");
            menu.getMenu().add("Khác");
            menu.setOnMenuItemClickListener(item -> {
                etGender.setText(item.getTitle());
                return true;
            });
            menu.show();
        });

        // Ngày sinh: DatePickerDialog -> dd/MM/yyyy
        etDob.setOnClickListener(v -> openDobPicker());
        etDob.setOnFocusChangeListener((view, hasFocus) -> { if (hasFocus) openDobPicker(); });
    }

    private void openDobPicker() {
        Calendar c = Calendar.getInstance();

        // Nếu đã có sẵn giá trị dd/MM/yyyy thì prefill
        String cur = trim(etDob.getText().toString());
        if (!TextUtils.isEmpty(cur)) {
            String[] p = cur.replace('.', '/').replace('-', '/').split("/");
            if (p.length == 3) {
                try {
                    int d = Integer.parseInt(p[0]);
                    int m = Integer.parseInt(p[1]) - 1;
                    int y = Integer.parseInt(p[2]);
                    c.set(y, m, d);
                } catch (Exception ignore) { }
            }
        }

        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);

        new android.app.DatePickerDialog(
                this,
                (view, year, month, day) ->
                        etDob.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year)),
                y, m, d
        ).show();
    }

    // ============== Helpers ==============
    private String nz(String s) { return s == null ? "" : s; }
    private String trim(String s) { return s == null ? "" : s.trim(); }
    private void toast(String m) { Toast.makeText(this, m, Toast.LENGTH_SHORT).show(); }
    private String emptyToNull(String s) { return TextUtils.isEmpty(s) ? null : s; }

    // Gender mapping
    private String mapGenderToDisplay(String g) {
        if (g == null) return "";
        String v = g.trim().toLowerCase();
        if (v.equals("male") || v.equals("nam")) return "Nam";
        if (v.equals("female") || v.equals("nữ") || v.equals("nu")) return "Nữ";
        return "Khác";
    }

    private String mapGenderToApi(String ui) {
        if (ui == null) return null;
        String v = ui.trim().toLowerCase();
        if (v.equals("nam") || v.equals("male")) return "male";
        if (v.equals("nữ") || v.equals("nu") || v.equals("female")) return "female";
        return "other";
    }

    // Dob: UI dd/MM/yyyy -> API yyyy-MM-dd
    private String toIsoDate(String display) {
        if (TextUtils.isEmpty(display)) return null;
        String s = display.replace('.', '/').replace('-', '/');
        String[] p = s.split("/");
        if (p.length == 3) {
            // Nếu người dùng nhập yyyy/MM/dd
            if (p[0].length() == 4) return p[0] + "-" + pad2(p[1]) + "-" + pad2(p[2]);
            // Mặc định dd/MM/yyyy
            return p[2] + "-" + pad2(p[1]) + "-" + pad2(p[0]);
        }
        if (display.matches("\\d{4}-\\d{2}-\\d{2}")) return display; // đã đúng yyyy-MM-dd
        return null;
    }

    // Dob: API ISO "...T...Z" -> UI dd/MM/yyyy
    private String formatDobIsoZToDisplay(String isoZ) {
        if (TextUtils.isEmpty(isoZ)) return "";
        try {
            // 1️⃣ Parse ISO ở UTC
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            in.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = in.parse(isoZ);

            // 2️⃣ Đổi sang timezone local của thiết bị (VD: Asia/Ho_Chi_Minh)
            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            out.setTimeZone(TimeZone.getDefault());

            // 3️⃣ Trả ra dạng "09/11/2024"
            return out.format(d);
        } catch (Exception e) {
            // fallback: vẫn cắt ngày trước 'T' nếu format lạ
            String[] parts = isoZ.split("T");
            String date = parts.length > 0 ? parts[0] : isoZ;
            String[] p = date.split("-");
            if (p.length == 3) return pad2(p[2]) + "/" + pad2(p[1]) + "/" + p[0];
            return isoZ;
        }
    }
    private String pad2(String x) {
        if (x == null) return "";
        return (x.length() == 1) ? "0" + x : x;
    }
}
