package com.lksnext.parkingplantilla.data;
import android.content.Context;
import android.content.SharedPreferences;


public class AuthRepository {
    private final SharedPreferences sharedPreferences;

    public AuthRepository(Context context) {
        sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }

    public void logout() {
        // Borrar token, usuario, etc.
        sharedPreferences.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.contains("token"); // o como manejes el login
    }
}
