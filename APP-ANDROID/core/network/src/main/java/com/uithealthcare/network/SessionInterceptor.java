package com.uithealthcare.network;


import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class SessionInterceptor implements Interceptor {
    public interface TokenProvider { String getToken(); }

    private final TokenProvider provider;
    public SessionInterceptor(TokenProvider provider) { this.provider = provider; }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String token = provider.getToken();

        String path = req.url().encodedPath(); // ví dụ: /api/auth/login hay /api/patient/profile

        if (token == null || token.isEmpty()) {
            // 👉 Chỉ cảnh báo nếu KHÔNG phải endpoint auth
            if (!path.startsWith("/api/auth/")) {
                android.util.Log.w("AUTH", "No token → " + req.method() + " " + req.url());
            } else {
                // login/register thì không cần warn
                android.util.Log.d("AUTH", "No token (auth endpoint) → " + req.method() + " " + req.url());
            }
            return chain.proceed(req);
        }

        Request newReq = req.newBuilder()
                .addHeader("Authorization", token)
                .build();

        // Log gọn, không in full token
        android.util.Log.d("AUTH", "Attach Authorization: "
                + token.substring(0, Math.min(20, token.length())) + "… → " + req.url());

        return chain.proceed(newReq);
    }

}
