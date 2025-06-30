package com.lksnext.parkingplantilla.data;

import android.util.Log;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingplantilla.domain.Callback;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class DataRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private static DataRepository instance;

    private DataRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    // Singleton: instancia única
    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public void login(String email, String pass, Callback callback) {
        try {
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure();
                        }
                    });
        } catch (Exception e) {
            Log.e("DataRepository", "Error en login: ", e);
            callback.onFailure();
        }
    }

    public void loginWithCredential(AuthCredential credential, Callback callback) {
        try {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure();
                        }
                    });
        } catch (Exception e) {
            Log.e("DataRepository", "Error en loginWithCredential: ", e);
            callback.onFailure();
        }
    }

    public void register(String email, String password, String phone, Callback callback) {
        try {
            if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                Log.e("DataRepository", "Email or password is empty");
                callback.onFailure();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", email);
                                if (phone != null && !phone.isEmpty()) {
                                    userData.put("phone", phone);
                                }
                                Log.d("DataRepository", "User data to save: " + userData.toString());

                                db.collection("users")  // Usa la instancia db
                                        .document(user.getUid())
                                        .set(userData)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                callback.onSuccess();
                                            } else {
                                                callback.onFailure();
                                            }
                                        });
                            } else {
                                callback.onFailure();
                            }
                        } else {
                            callback.onFailure();
                        }
                    });
        } catch (Exception e) {
            Log.e("DataRepository", "Error en register: ", e);
            callback.onFailure();
        }
    }

    public void addReserva(String plaza, String hora, String parking, Callback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onFailure();
            return;
        }

        Map<String, Object> reserva = new HashMap<>();
        reserva.put("plaza", plaza);
        reserva.put("hora", hora);
        reserva.put("parking", parking);

        db.collection("users")
                .document(user.getUid())
                .collection("reservas")
                .add(reserva)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("DataRepository", "Error al añadir reserva", e);
                    callback.onFailure();
                });
    }

    public void addVehiculo(String matricula, String marca, String modelo, String color, Callback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onFailure();
            return;
        }

        Map<String, Object> vehiculo = new HashMap<>();
        vehiculo.put("matricula", matricula);
        vehiculo.put("marca", marca);
        vehiculo.put("modelo", modelo);
        vehiculo.put("color", color);

        db.collection("users")
                .document(user.getUid())
                .collection("vehiculos")
                .add(vehiculo)
                .addOnSuccessListener(documentReference -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e("DataRepository", "Error al añadir vehículo", e);
                    callback.onFailure();
                });
    }

    public FirebaseUser getCurrentUser() {
        try {
            FirebaseUser user = mAuth.getCurrentUser();
            Log.d("DataRepository", "getCurrentUser: " + user);
            return user;
        } catch (Exception e) {
            Log.e("DataRepository", "Error en getCurrentUser: ", e);
            return null;
        }
    }
}
