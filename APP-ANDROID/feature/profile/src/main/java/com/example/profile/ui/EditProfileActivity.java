package com.example.profile.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.profile.R;
import com.example.profile.data.PatientApi;
import com.uithealthcare.network.RetrofitProvider;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnBack;
    private TextView tvFullNameValue, tvGenderValue, tvDobValue, tvPhoneValue, tvAddress, tvEmailValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // Ánh xạ view
        imgAvatar       = findViewById(R.id.imgAvatar);
        btnBack         = findViewById(R.id.btnBack);
        tvFullNameValue = findViewById(R.id.tvFullNameValue);
        tvGenderValue   = findViewById(R.id.tvGenderValue);
        tvDobValue      = findViewById(R.id.tvDobValue);
        tvPhoneValue    = findViewById(R.id.tvPhoneValue);
        tvAddress       = findViewById(R.id.tvAddress);
        tvEmailValue    = findViewById(R.id.tvEmailValue);

        if (btnBack != null) btnBack.setOnClickListener(v -> onBackPressed());

        // Kiểm tra token và khôi phục nếu cần
        ensureToken();

        // Gọi API hồ sơ
        loadProfile();
    }

    private void ensureToken() {
        SessionManager sm = new SessionManager(this);
        String bearer = sm.getBearer();
        if (bearer == null) {
            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            String raw = sp.getString("access_token", null);
            if (raw != null && !raw.isEmpty()) {
                sm.saveBearer(raw);
            }
        }
    }

    private PatientApi api() {
        SessionInterceptor.TokenProvider provider = () -> new SessionManager(EditProfileActivity.this).getBearer();
        return RetrofitProvider.get(provider).create(PatientApi.class);
    }

    private void loadProfile() {
        api().getMyProfile().enqueue(new Callback<PatientApi.ProfileResp>() {
            @Override
            public void onResponse(Call<PatientApi.ProfileResp> call, Response<PatientApi.ProfileResp> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;

                PatientApi.ProfileResp body = resp.body();
                PatientApi.Patient p = null;

                if (body.data != null) {
                    if (body.data.user != null) p = body.data.user;
                    else if (body.data.profile != null) p = body.data.profile;
                }
                if (p == null) {
                    if (body.userRoot != null) p = body.userRoot;
                    else if (body.profileRoot != null) p = body.profileRoot;
                }

                if (p != null) bind(p);
            }

            @Override
            public void onFailure(Call<PatientApi.ProfileResp> call, Throwable t) {
                // Không cần Toast/log
            }
        });
    }

    private void bind(PatientApi.Patient p) {
        tvFullNameValue.setText(orDash(p.fullName));
        tvGenderValue.setText(mapGender(p.gender));
        tvDobValue.setText(formatDob(p.dob));
        tvPhoneValue.setText(orDash(p.phone));
        tvAddress.setText(orDash(p.address));
        tvEmailValue.setText(orDash(p.email));
        // Có thể thêm load avatar nếu cần
    }

    // helpers
    private String orDash(String s) {
        return (s == null || s.trim().isEmpty()) ? "—" : s;
    }

    private String mapGender(String g) {
        if (g == null) return "—";
        String v = g.trim().toLowerCase();
        if (v.equals("male") || v.equals("nam")) return "Nam";
        if (v.equals("female") || v.equals("nữ") || v.equals("nu")) return "Nữ";
        return "Khác";
    }

    private String formatDob(String iso) {
        if (iso == null || iso.length() < 8) return orDash(iso);
        try {
            String s = iso.contains("/") ? iso.replace("/", "-") : iso;
            String[] p = s.split("-");
            if (p.length >= 3) return p[2] + "-" + p[1] + "-" + p[0];
            return iso;
        } catch (Exception e) {
            return iso;
        }
    }
}
