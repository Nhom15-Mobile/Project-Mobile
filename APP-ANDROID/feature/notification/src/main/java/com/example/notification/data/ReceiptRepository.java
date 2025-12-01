package com.example.notification.data;

import android.content.Context;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.uithealthcare.network.RetrofitProvider;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.SessionManager;

public class ReceiptRepository {
    private final ReceiptApi api;

    public interface ReceiptCallback {
        void onSuccess(ReceiptApi.Receipt r);
        void onError(String msg);
    }

    public ReceiptRepository(Context ctx) {
        SessionInterceptor.TokenProvider provider =
                () -> new SessionManager(ctx).getBearer();
        api = RetrofitProvider.get(provider).create(ReceiptApi.class);
    }

    public void getReceipt(String appointmentId, ReceiptCallback cb) {
        api.getReceipt(appointmentId).enqueue(new Callback<ReceiptApi.ReceiptResp>() {
            @Override
            public void onResponse(Call<ReceiptApi.ReceiptResp> call,
                                   Response<ReceiptApi.ReceiptResp> resp) {
                if (!resp.isSuccessful() || resp.body() == null || !resp.body().success) {
                    cb.onError("Không lấy được phiếu khám");
                    return;
                }
                cb.onSuccess(resp.body().data);
            }

            @Override
            public void onFailure(Call<ReceiptApi.ReceiptResp> call, Throwable t) {
                cb.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }
}
