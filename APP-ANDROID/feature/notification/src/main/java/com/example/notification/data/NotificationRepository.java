package com.example.notification.data;

import android.content.Context;

import androidx.annotation.Nullable;

import com.uithealthcare.network.RetrofitProvider;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.SessionManager;
import retrofit2.Callback;
import retrofit2.Call;
import retrofit2.Response;

public class NotificationRepository {

    private final NotificationApi api;

    public interface CallbackList {
        void onSuccess(NotificationApi.Data data);
        void onError(String message);
    }

    public NotificationRepository(Context ctx) {
        // TokenProvider sẽ được SessionInterceptor gọi mỗi khi gửi request
        SessionInterceptor.TokenProvider provider =
                () -> new SessionManager(ctx).getBearer();  // phải trả về "Bearer xxx"

        api = RetrofitProvider.get(provider).create(NotificationApi.class);
    }

    public void getNotifications(@Nullable String cursor,
                                 @Nullable Integer limit,
                                 @Nullable Boolean unreadOnly,
                                 CallbackList cb) {

        api.getNotifications(cursor, limit, unreadOnly)
                .enqueue(new Callback<NotificationApi.NotificationListResp>() {
                    @Override
                    public void onResponse(Call<NotificationApi.NotificationListResp> call,
                                           Response<NotificationApi.NotificationListResp> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            cb.onError("Lỗi server: " + response.code());
                            return;
                        }
                        NotificationApi.NotificationListResp body = response.body();
                        if (!body.success) {
                            cb.onError(body.message != null ? body.message : "Request thất bại");
                            return;
                        }
                        cb.onSuccess(body.data);
                    }

                    @Override
                    public void onFailure(Call<NotificationApi.NotificationListResp> call, Throwable t) {
                        cb.onError("Lỗi mạng: " + t.getMessage());
                    }
                });
    }
}
