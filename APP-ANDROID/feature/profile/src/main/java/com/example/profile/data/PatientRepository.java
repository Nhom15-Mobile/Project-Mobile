package com.example.profile.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.uithealthcare.network.RetrofitProvider;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientRepository {

    private final PatientApi api;

    public PatientRepository(Context ctx) {
        SessionInterceptor.TokenProvider provider =
                () -> new SessionManager(ctx).getBearer();
        this.api = RetrofitProvider.get(provider).create(PatientApi.class);
    }

    // Callback tối giản
    public interface RepoCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    // GET profile (khớp JSON của bạn: data + user)
    public void getProfile(RepoCallback<PatientApi.ProfileData> cb) {
        api.getMyProfile().enqueue(new Callback<PatientApi.GetProfileResp>() {
            @Override public void onResponse(Call<PatientApi.GetProfileResp> call, Response<PatientApi.GetProfileResp> resp) {
                if (!resp.isSuccessful() || resp.body() == null || resp.body().data == null) {
                    cb.onError("Không lấy được hồ sơ"); return;
                }
                cb.onSuccess(resp.body().data);
            }
            @Override public void onFailure(Call<PatientApi.GetProfileResp> call, Throwable t) {
                cb.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    // UPDATE profile (gửi emergencyContact từ UI “Số điện thoại”)
    public void updateProfile(UpdateArgs args, RepoCallback<PatientApi.ProfileData> cb) {
        PatientApi.UpdateReq body = new PatientApi.UpdateReq(
                // gender, dob, address, insuranceNumber, emergencyContact, phone, fullName, email
                nz(args.gender), nz(args.dob), nz(args.address),
                nz(args.insuranceNumber), nz(args.emergencyContact),
                nz(args.phone), null, null
        );
        api.updateMyProfile(body).enqueue(new Callback<PatientApi.UpdateResp>() {
            @Override public void onResponse(Call<PatientApi.UpdateResp> call, Response<PatientApi.UpdateResp> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    cb.onError("Cập nhật thất bại"); return;
                }
                // Server có thể trả data hoặc không; nếu không, gọi lại GET cho chắc
                if (resp.body().data != null) {
                    cb.onSuccess(resp.body().data);
                } else {
                    // fallback: reload
                    getProfile(cb);
                }
            }
            @Override public void onFailure(Call<PatientApi.UpdateResp> call, Throwable t) {
                cb.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    private static String nz(@Nullable String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    // Tham số update gọn gàng
    public static class UpdateArgs {
        public String gender;            // "male|female|other"
        public String dob;               // "yyyy-MM-dd"
        public String address;
        public String insuranceNumber;   // optional
        public String emergencyContact;  // map từ etPhone
        public String phone;             // để null nếu không dùng

        public UpdateArgs(String gender, String dob, String address, String emergencyContact) {
            this.gender = gender;
            this.dob = dob;
            this.address = address;
            this.emergencyContact = emergencyContact;
        }

        public UpdateArgs withInsurance(String insurance) { this.insuranceNumber = insurance; return this; }
        public UpdateArgs withPhone(String phone) { this.phone = phone; return this; }
    }
}
