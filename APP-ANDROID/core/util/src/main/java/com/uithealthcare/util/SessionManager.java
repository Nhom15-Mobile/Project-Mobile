package com.uithealthcare.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF = "session_pref";
    private static final String KEY_TOKEN = "auth_token"; // "Bearer <jwt>"

    private final SharedPreferences sp;

    public SessionManager(Context ctx){
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void saveBearer(String jwt){
        sp.edit().putString(KEY_TOKEN, jwt == null ? null : "Bearer " + jwt).apply();
    }

    public String getBearer(){ return sp.getString(KEY_TOKEN, null); }

    public boolean isLoggedIn() { return getBearer() != null; }

    public void clear(){ sp.edit().clear().apply(); }

    // ✅ Không phụ thuộc vào feature: truyền class màn Login từ nơi gọi
    public void logout(Context context, Class<?> loginActivityClass) {
        clear();
        Intent i = new Intent(context, loginActivityClass);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }
}
