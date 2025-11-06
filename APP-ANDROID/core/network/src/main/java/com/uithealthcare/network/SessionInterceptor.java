package com.uithealthcare.network;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** Chèn header Authorization nếu có token (được set từ feature/auth). */
public class SessionInterceptor implements Interceptor {
    public interface TokenProvider {
        String getToken(); // trả về "Bearer xxx" hoặc null
    }

    private final TokenProvider provider;

    public SessionInterceptor(TokenProvider provider) {
        this.provider = provider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String token = provider.getToken();
        if (token != null && !token.isEmpty()) {
            req = req.newBuilder()
                    .addHeader("Authorization", token)
                    .build();
        }
        return chain.proceed(req);
    }
}
