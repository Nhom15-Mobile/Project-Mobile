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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.profile.R;
import com.example.profile.data.PatientApi;
import com.example.profile.data.PatientRepository;   // ★ dùng Repository
import com.uithealthcare.util.SessionManager;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnBack;

    // READ-ONLY
    private TextView tvFullName, tvEmail;

    // Editable
    private EditText etGender, etDob, etPhone, etAddress;

    // ★ Repo
    private PatientRepository repo;

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

        // Khởi tạo Repo
        repo = new PatientRepository(this);

        // (Tuỳ project) đảm bảo token đã có (nếu bạn cần tự nạp)
        ensureToken();

        // Tải hồ sơ hiện tại qua Repo
        loadProfile();

        // Lưu thay đổi qua Repo
        findViewById(R.id.btnSave).setOnClickListener(v -> saveProfile());
    }

    private void ensureToken() {
        // Nếu SessionManager đã tự xử lý thì có thể bỏ
        SessionManager sm = new SessionManager(this);
        String bearer = sm.getBearer();
        if (bearer == null) {
            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String raw = sp.getString("access_token", null);
            if (!TextUtils.isEmpty(raw)) sm.saveBearer(raw);
        }
    }

    // ===== GET profile (Repository) =====
    private void loadProfile() {
        repo.getProfile(new PatientRepository.RepoCallback<PatientApi.ProfileData>() {
            @Override public void onSuccess(PatientApi.ProfileData data) { bind(data); }
            @Override public void onError(String message) { toast(message); }
        });
    }

    // ===== Bind UI từ ProfileData =====
    private void bind(PatientApi.ProfileData d) {
        // READ-ONLY
        if (d.user != null) {
            tvFullName.setText(nz(d.user.fullName));
            tvEmail.setText(nz(d.user.email));
        }

        // etPhone hiển thị emergencyContact (yêu cầu của bạn)
        etPhone.setText(nz(d.emergencyContact));

        // Editable
        etGender.setText(mapGenderToDisplay(d.gender));
        etDob.setText(formatDobIsoZToDisplay(d.dob));
        etAddress.setText(nz(d.address));
    }

    // ===== SAVE profile (Repository) =====
    private void saveProfile() {
        String genderApi = mapGenderToApi(trim(etGender.getText().toString())); // male/female/other
        String dobIso    = toIsoDate(trim(etDob.getText().toString()));         // yyyy-MM-dd
        String emergency = trim(etPhone.getText().toString());                  // etPhone -> emergencyContact
        String address   = trim(etAddress.getText().toString());

        findViewById(R.id.btnSave).setEnabled(false);

        PatientRepository.UpdateArgs args =
                new PatientRepository.UpdateArgs(
                        emptyToNull(genderApi),
                        emptyToNull(dobIso),
                        emptyToNull(address),
                        emptyToNull(emergency)
                );
        // Nếu cần: args.withInsurance(...).withPhone(null);

        repo.updateProfile(args, new PatientRepository.RepoCallback<PatientApi.ProfileData>() {
            @Override public void onSuccess(PatientApi.ProfileData data) {
                findViewById(R.id.btnSave).setEnabled(true);
                bind(data);
                toast("Đã lưu thay đổi");
            }
            @Override public void onError(String message) {
                findViewById(R.id.btnSave).setEnabled(true);
                toast(message);
            }
        });
    }

    // ===== Pickers =====
    private void setupPickers() {
        etGender.setOnClickListener(v -> {
            android.widget.PopupMenu menu = new android.widget.PopupMenu(this, v);
            menu.getMenu().add("Nam");
            menu.getMenu().add("Nữ");
            menu.getMenu().add("Khác");
            menu.setOnMenuItemClickListener(item -> { etGender.setText(item.getTitle()); return true; });
            menu.show();
        });

        etDob.setOnClickListener(v -> openDobPicker());
        etDob.setOnFocusChangeListener((view, hasFocus) -> { if (hasFocus) openDobPicker(); });
    }

    private void openDobPicker() {
        Calendar c = Calendar.getInstance();

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

    // ===== Helpers =====
    private String nz(String s) { return s == null ? "" : s; }
    private String trim(String s) { return s == null ? "" : s.trim(); }
    private void toast(String m) { Toast.makeText(this, m, Toast.LENGTH_SHORT).show(); }
    private String emptyToNull(String s) { return TextUtils.isEmpty(s) ? null : s; }

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

    // UI dd/MM/yyyy -> API yyyy-MM-dd
    private String toIsoDate(String display) {
        if (TextUtils.isEmpty(display)) return null;
        String s = display.replace('.', '/').replace('-', '/');
        String[] p = s.split("/");
        if (p.length == 3) {
            if (p[0].length() == 4) return p[0] + "-" + pad2(p[1]) + "-" + pad2(p[2]); // yyyy/MM/dd
            return p[2] + "-" + pad2(p[1]) + "-" + pad2(p[0]);                          // dd/MM/yyyy
        }
        if (display.matches("\\d{4}-\\d{2}-\\d{2}")) return display;
        return null;
    }

    // API ISO "...T...Z" -> UI dd/MM/yyyy (fix lệch ngày)
    private String formatDobIsoZToDisplay(String isoZ) {
        if (TextUtils.isEmpty(isoZ)) return "";
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            in.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = in.parse(isoZ);

            SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            out.setTimeZone(TimeZone.getDefault());
            return out.format(d);
        } catch (Exception e) {
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
