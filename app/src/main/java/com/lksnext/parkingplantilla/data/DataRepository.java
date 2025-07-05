package com.lksnext.parkingplantilla.data;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackWithReserva;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.domain.enu.PlazaType;
import com.lksnext.parkingplantilla.receiver.NotificationHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
public class DataRepository {
    private static final String KEY_ID = "idReserva";
    private static final String KEY_FECHA = "fecha";
    private static final String KEY_USUARIO = "usuario";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_PLAZA = "plaza";
    private static final String KEY_HORA = "hora";
    private static final String KEY_TIPO_PLAZA = "tipoPlaza";

    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private static DataRepository instance;

    private DataRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

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
                        if (task.isSuccessful()) callback.onSuccess();
                        else callback.onFailure();
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
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                crearUsuarioFirestoreSiNoExiste(user);
                            }
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
    private void crearUsuarioFirestoreSiNoExiste(FirebaseUser user) {
        DocumentReference userRef = db.collection("users").document(user.getUid());
        userRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", user.getUid());
                userData.put("nombre", user.getDisplayName() != null ? user.getDisplayName() : "");
                userData.put("email", user.getEmail() != null ? user.getEmail() : "");
                userData.put("telefono", "");
                userData.put("fechaRegistro", FieldValue.serverTimestamp());

                userRef.set(userData)
                        .addOnSuccessListener(unused -> Log.d("DataRepository", "Usuario creado en Firestore"))
                        .addOnFailureListener(e -> Log.e("DataRepository", "Error al crear usuario", e));
            } else {
                Log.d("DataRepository", "Usuario ya existe en Firestore");
            }
        }).addOnFailureListener(e -> Log.e("DataRepository", "Error comprobando usuario Firestore", e));
    }

    public void register(String email, String password, String phone, Callback callback) {
        if (isNullOrEmpty(email) || isNullOrEmpty(password)) {
            Log.e("DataRepository", "Email or password is empty");
            callback.onFailure();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("DataRepository", "Error creating user: " + task.getException());
                        callback.onFailure();
                        return;
                    }

                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) {
                        Log.e("DataRepository", "User is null after creation");
                        callback.onFailure();
                        return;
                    }

                    saveUserData(user.getUid(), email, phone, callback);
                });
    }

    private void saveUserData(String uid, String email, String phone, Callback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        if (!isNullOrEmpty(phone)) userData.put("phone", phone);

        db.collection("users").document(uid).set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onSuccess();
                    else callback.onFailure();
                });
    }

    public void crearPlazasIniciales(Callback callback) {
        db.collection("plazas").limit(1).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Log.d("crearPlazas", "Las plazas ya existen.");
                        callback.onSuccess();
                    } else {
                        crearTodasLasPlazas(callback);
                    }
                })
                .addOnFailureListener(e -> callback.onFailure());
    }

    private void crearTodasLasPlazas(Callback callback) {
        Map<String, Integer> tipos = new LinkedHashMap<>();
        tipos.put("Coche", 50);
        tipos.put("Moto", 25);
        tipos.put("Discapacitado", 4);
        tipos.put("Electrico", 4);

        var batch = db.batch();

        for (Map.Entry<String, Integer> entry : tipos.entrySet()) {
            String tipo = entry.getKey();
            int plazasPorTipo = entry.getValue();

            for (int i = 1; i <= plazasPorTipo; i++) {
                String docId = tipo.substring(0, 3).toUpperCase() + String.format("%03d", i);

                Map<String, Object> plaza = new HashMap<>();
                plaza.put("tipoPlaza", tipo);
                plaza.put("codigo", docId);
                plaza.put("ocupada", false);

                var docRef = db.collection("plazas").document(docId);
                batch.set(docRef, plaza);
            }
        }

        batch.commit()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public void addVehiculo(String matricula, String pollutionType, String marca, Uri imageUri, Callback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Log.e("DataRepository", "Usuario no autenticado");
            callback.onFailure();
            return;
        }

        if (isNullOrEmpty(matricula)) {
            Log.e("DataRepository", "Matrícula es null o vacía");
            callback.onFailure();
            return;
        }

        if (imageUri == null) {
            Log.e("DataRepository", "Imagen no seleccionada");
            callback.onFailure();
            return;
        }

        Log.d("DataRepository", "UID: " + user.getUid());
        Log.d("DataRepository", "Subiendo imagen...");

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("users/" + user.getUid() + "/vehicles/" + matricula + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    Map<String, Object> vehiculo = new HashMap<>();
                    vehiculo.put("matricula", matricula);
                    vehiculo.put("pollutionType", pollutionType);
                    vehiculo.put("marca", marca);
                    vehiculo.put("imageUrl", downloadUri.toString());

                    Log.d("DataRepository", "Guardando en Firestore: " + vehiculo);

                    db.collection("users")
                            .document(user.getUid())
                            .collection("vehiculos")
                            .document(matricula)
                            .set(vehiculo)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("DataRepository", "Vehículo guardado correctamente");
                                callback.onSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("DataRepository", "Error guardando vehículo: ", e);
                                callback.onFailure();
                            });

                }).addOnFailureListener(e -> {
                    Log.e("DataRepository", "Error obteniendo URL imagen: ", e);
                    callback.onFailure();
                }))
                .addOnFailureListener(e -> {
                    Log.e("DataRepository", "Error subiendo imagen: ", e);
                    callback.onFailure();
                });
    }





    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void comprobarYCrearReserva(Context context, String fecha, List<String> horas, String tipoPlaza, CallbackWithReserva callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onFailure();
            return;
        }

        db.collection("plazas")
                .whereEqualTo("tipoPlaza", tipoPlaza)
                .get()
                .addOnSuccessListener(plazasSnapshot -> {
                    if (plazasSnapshot.isEmpty()) {
                        callback.onFailure();
                        return;
                    }

                    List<String> codigosPlazas = new ArrayList<>();
                    for (var plazaDoc : plazasSnapshot) {
                        String codigo = plazaDoc.getId();
                        if (codigo != null) codigosPlazas.add(codigo);
                    }

                    buscarPlazaLibre(context, codigosPlazas, 0, fecha, horas, callback, user, tipoPlaza);
                })
                .addOnFailureListener(e -> callback.onFailure());
    }

    private void buscarPlazaLibre(Context context, List<String> codigos, int index, String fecha, List<String> horas, CallbackWithReserva callback, FirebaseUser user, String tipoPlaza) {
        if (index >= codigos.size()) {
            callback.onFailure();
            return;
        }

        String plazaCodigo = codigos.get(index);

        db.collection("reservas")
                .whereEqualTo(KEY_FECHA, fecha)
                .whereEqualTo(KEY_PLAZA, plazaCodigo)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (existeSolapamiento(querySnapshot, horas)) {
                        buscarPlazaLibre(context, codigos, index + 1, fecha, horas, callback, user, tipoPlaza);
                        return;
                    }

                    Map<String, Object> reservaData = new HashMap<>();
                    reservaData.put(KEY_FECHA, fecha);
                    reservaData.put(KEY_USUARIO, user.getEmail());
                    reservaData.put(KEY_UUID, user.getUid());
                    reservaData.put(KEY_PLAZA, plazaCodigo);
                    reservaData.put(KEY_HORA, horas);
                    reservaData.put(KEY_TIPO_PLAZA, tipoPlaza);

                    db.collection("reservas").add(reservaData)
                            .addOnSuccessListener(r -> {
                                String idGenerado = r.getId();
                                db.collection("reservas").document(idGenerado).update(KEY_ID, idGenerado);

                                db.collection("plazas").document(plazaCodigo)
                                        .update("ocupada", true)
                                        .addOnSuccessListener(aVoid -> {
                                            // ✅ Construye Reserva real para devolverla
                                            Plaza plaza = new Plaza(plazaCodigo, parseTipoPlaza(tipoPlaza));
                                            Hora hora = new Hora(horas);
                                            Timestamp fechaTimestamp = parseFecha(fecha);
                                            Reserva reserva = new Reserva(idGenerado, fechaTimestamp, user.getEmail(), user.getUid(), plaza, hora);

                                            NotificationHelper.programarNotificaciones(context, reserva);

                                            callback.onSuccess(reserva);
                                        })
                                        .addOnFailureListener(e -> callback.onFailure());
                            })
                            .addOnFailureListener(e -> callback.onFailure());
                });
    }



    private void buscarPlazaLibre(Context context, List<String> codigos, int index, String fecha, List<String> horas, Callback callback, FirebaseUser user, String tipoPlaza) {
        if (index >= codigos.size()) {
            callback.onFailure();
            return;
        }

        String plazaCodigo = codigos.get(index);

        db.collection("reservas")
                .whereEqualTo(KEY_FECHA, fecha)
                .whereEqualTo(KEY_PLAZA, plazaCodigo)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (existeSolapamiento(querySnapshot, horas)) {
                        buscarPlazaLibre(context, codigos, index + 1, fecha, horas, callback, user, tipoPlaza);
                        return;
                    }

                    Map<String, Object> reserva = new HashMap<>();
                    reserva.put(KEY_FECHA, fecha);
                    reserva.put(KEY_USUARIO, user.getEmail());
                    reserva.put(KEY_UUID, user.getUid());
                    reserva.put(KEY_PLAZA, plazaCodigo);
                    reserva.put(KEY_HORA, horas);
                    reserva.put(KEY_TIPO_PLAZA, tipoPlaza);

                    db.collection("reservas").add(reserva)
                            .addOnSuccessListener(r -> {
                                String idGenerado = r.getId();
                                db.collection("reservas").document(idGenerado).update(KEY_ID, idGenerado);

                                db.collection("plazas").document(plazaCodigo)
                                        .update("ocupada", true)
                                        .addOnSuccessListener(aVoid -> {

                                            NotificationHelper.programarNotificacionesReserva(context, fecha, horas);
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> callback.onFailure());
                            })
                            .addOnFailureListener(e -> callback.onFailure());
                });
    }

    private boolean existeSolapamiento(QuerySnapshot querySnapshot, List<String> horasSolicitadas) {
        for (var doc : querySnapshot) {
            List<String> horasOcupadas = (List<String>) doc.get("hora");
            if (horasOcupadas == null) continue;
            for (String hora : horasSolicitadas) {
                if (horasOcupadas.contains(hora)) return true;
            }
        }
        return false;
    }

    public void getReservasUsuario(String uid, CallbackWithResult<List<Reserva>> callback) {
        db.collection("reservas").whereEqualTo(KEY_UUID, uid).get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Reserva> reservas = new ArrayList<>();
                    for (var doc : querySnapshot) {
                        try {
                            String idReserva = doc.getId(); // ✅ usa siempre el ID real
                            Timestamp fecha = parseFecha(doc.get(KEY_FECHA));
                            String usuario = doc.getString(KEY_USUARIO);
                            String uuid = doc.getString(KEY_UUID);
                            String idPlaza = doc.getString(KEY_PLAZA);
                            PlazaType tipoPlaza = parseTipoPlaza(doc.getString(KEY_TIPO_PLAZA));
                            List<String> horasList = castToListString(doc.get(KEY_HORA));

                            if (fecha == null || usuario == null || uuid == null || horasList == null) continue;

                            Plaza plaza = new Plaza(idPlaza, tipoPlaza);
                            Hora hora = new Hora(horasList);
                            reservas.add(new Reserva(idReserva, fecha, usuario, uuid, plaza, hora));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    callback.onSuccess(reservas);
                })
                .addOnFailureListener(callback::onFailure);
    }

    private Timestamp parseFecha(Object fechaObj) {
        if (fechaObj instanceof Timestamp) return (Timestamp) fechaObj;
        if (fechaObj instanceof String) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = sdf.parse((String) fechaObj);
                if (date != null) return new Timestamp(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private PlazaType parseTipoPlaza(String tipoPlazaStr) {
        if (tipoPlazaStr == null) return PlazaType.COCHE;
        switch (tipoPlazaStr.toLowerCase()) {
            case "electrico": return PlazaType.ELECTRICO;
            case "discapacitado": return PlazaType.DISCAPACITADO;
            case "moto": return PlazaType.MOTO;
            default: return PlazaType.COCHE;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> castToListString(Object obj) {
        if (obj instanceof List<?>) {
            try { return (List<String>) obj; }
            catch (ClassCastException e) { e.printStackTrace(); }
        }
        return null;
    }

    public void borrarReservaYLiberarPlaza(String IdReserva, String plazaId, Callback callback) {
        Log.d("DataRepository", "Borrando reserva ID: " + IdReserva);
        db.collection("reservas").document(IdReserva).delete()
                .addOnSuccessListener(aVoid -> {
                    db.collection("plazas").document(plazaId)
                            .update("ocupada", false)
                            .addOnSuccessListener(aVoid2 -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure());
                })
                .addOnFailureListener(e -> callback.onFailure());
    }
    public void updateHoraReserva(String idReserva, List<String> nuevaHora, Callback callback) {
        db.collection("reservas").document(idReserva)
                .update("hora", nuevaHora)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public void getCurrentUserData(String uid, CallbackWithResult<Map<String, Object>> callback) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        callback.onSuccess(documentSnapshot.getData());
                    } else {
                        callback.onFailure(new Exception("No user data found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void logout() {
        try {
            mAuth.signOut();
        } catch (Exception e) {
            Log.e("DataRepository", "Error during logout: ", e);
        }
    }

    public void updateUserProfile(String uid, String name, String email, String phone, CallbackWithResult<Boolean> callbackWithResult) {
        Map<String, Object> userData = new HashMap<>();
        if (name != null && !name.isEmpty()) userData.put("nombre", name);
        if (email != null && !email.isEmpty()) userData.put("email", email);
        if (phone != null && !phone.isEmpty()) userData.put("telefono", phone);

        db.collection("users").document(uid).update(userData)
                .addOnSuccessListener(aVoid -> callbackWithResult.onSuccess(true))
                .addOnFailureListener(e -> callbackWithResult.onFailure(e));

    }

    public void uploadProfileImage(String uid, Uri selectedImageUri, CallbackWithResult<String> callbackWithResult) {
        if (selectedImageUri == null) {
            callbackWithResult.onFailure(new Exception("Selected image URI is null"));
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profile_images/" + uid + ".jpg");

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();


                    FirebaseFirestore.getInstance()
                            .collection("users").document(uid)
                            .update("imageUrl", downloadUrl)
                            .addOnSuccessListener(aVoid -> callbackWithResult.onSuccess(downloadUrl))
                            .addOnFailureListener(callbackWithResult::onFailure);

                }).addOnFailureListener(callbackWithResult::onFailure))
                .addOnFailureListener(callbackWithResult::onFailure);
    }





}
