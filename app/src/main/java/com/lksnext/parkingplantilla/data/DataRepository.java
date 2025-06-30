// app/src/main/java/com/lksnext/parkingplantilla/data/DataRepository.java
package com.lksnext.parkingplantilla.data;

import android.util.Log;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.domain.Callback;
import com.google.firebase.auth.FirebaseAuth;

public class DataRepository {

    private static DataRepository instance;
    private FirebaseAuth mAuth;

    private DataRepository() {
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public void login(String email, String pass, Callback callback) {
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }
                });
    }

    // Login con credenciales OAuth (Google)
    public void loginWithCredential(AuthCredential credential, Callback callback) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }
                });
    }

    public FirebaseUser getCurrentUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.d("DataRepository", "getCurrentUser: " + user);
        return user;
    }
}