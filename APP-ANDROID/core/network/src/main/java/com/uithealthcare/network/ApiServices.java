package com.uithealthcare.network;

public final class ApiServices {
    private ApiServices() {
    }

    public static <T> T create(Class<T> serviceClass,
                               SessionInterceptor.TokenProvider provider) {
        return RetrofitProvider.get(provider).create(serviceClass);
    }

    public static <T> T createOCR(Class<T> serviceClass,
                                  SessionInterceptor.TokenProvider provider) {
        return RetrofitProvider.getOCR(provider).create(serviceClass);
    }
}
