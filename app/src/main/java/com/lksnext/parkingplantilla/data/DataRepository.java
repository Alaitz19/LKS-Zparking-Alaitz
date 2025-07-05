package com.lksnext.parkingplantilla.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackWithReserva;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.domain.ReservaRequest;
import com.lksnext.parkingplantilla.domain.Vehicle;
import com.lksnext.parkingplantilla.domain.enu.PlazaType;
import com.lksnext.parkingplantilla.receiver.NotificationHelper;
import com.lksnext.parkingplantilla.util.FirestoreConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataRepository {

    private static final String TAG = FirestoreConstants.TAG;

    private final FirebaseFirestore db;
    private final FirebaseAuth mAuth;
    private ListenerRegistration vehiclesListener;

    //Para testear se ha hecho sin singleton, pero en una app real deberías usar un singleton para evitar múltiples instancias de DataRepository.

    public DataRepository(FirebaseFirestore db, FirebaseAuth mAuth) {
        this.db = db;
        this.mAuth = mAuth;
    }



    public void login(String email, String password, Callback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onSuccess();
                    else callback.onFailure();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login failed", e);
                    callback.onFailure();
                });
    }

    public void loginWithCredential(AuthCredential credential, Callback callback) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) crearUsuarioFirestoreSiNoExiste(user);
                        callback.onSuccess();
                    } else callback.onFailure();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Login with credential failed", e);
                    callback.onFailure();
                });
    }

    private void crearUsuarioFirestoreSiNoExiste(FirebaseUser user) {
        DocumentReference userRef = db.collection(FirestoreConstants.USERS).document(user.getUid());
        userRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", user.getUid());
                userData.put(FirestoreConstants.NOMBRE, safeString(user.getDisplayName()));
                userData.put(FirestoreConstants.EMAIL, safeString(user.getEmail()));
                userData.put(FirestoreConstants.TELEFONO, "");
                userData.put("fechaRegistro", FieldValue.serverTimestamp());

                userRef.set(userData)
                        .addOnSuccessListener(unused -> Log.d(TAG, "Usuario creado"))
                        .addOnFailureListener(e -> Log.e(TAG, "Error creando usuario", e));
            }
        });
    }

    public void register(String email, String password, String phone, Callback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onFailure();
                        return;
                    }
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) {
                        callback.onFailure();
                        return;
                    }
                    saveUserData(user.getUid(), email, phone, callback);
                });
    }

    private void saveUserData(String uid, String email, String phone, Callback callback) {
        Map<String, Object> userData = new HashMap<>();
        userData.put(FirestoreConstants.EMAIL, email);
        if (phone != null && !phone.isEmpty()) {
            userData.put(FirestoreConstants.TELEFONO, phone);
        }
        db.collection(FirestoreConstants.USERS).document(uid).set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) callback.onSuccess();
                    else callback.onFailure();
                });
    }

    public String getUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            return user.getDisplayName() + "!";
        }
        return "Usuario!";
    }

    public void logout() {
        mAuth.signOut();
    }


    public void crearPlazasIniciales(Callback callback) {
        db.collection(FirestoreConstants.PLAZAS).limit(1).get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) callback.onSuccess();
                    else crearTodasLasPlazas(callback);
                })
                .addOnFailureListener(e -> callback.onFailure());
    }

    private void crearTodasLasPlazas(Callback callback) {
        Map<String, Integer> tipos = Map.of(
                "Coche", 50, "Moto", 25, "Discapacitado", 4, "Electrico", 4
        );
        var batch = db.batch();
        for (var entry : tipos.entrySet()) {
            String tipo = entry.getKey();
            for (int i = 1; i <= entry.getValue(); i++) {
                String docId = tipo.substring(0, 3).toUpperCase(Locale.ROOT) + String.format(Locale.ROOT, "%03d", i);
                Map<String, Object> plaza = Map.of(
                        FirestoreConstants.TIPO_PLAZA, tipo,
                        "codigo", docId,
                        FirestoreConstants.OCUPADA, false
                );
                batch.set(db.collection(FirestoreConstants.PLAZAS).document(docId), plaza);
            }
        }
        batch.commit()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }

    public void getPlazasLibres(CallbackWithResult<List<Plaza>> callback) {
        db.collection(FirestoreConstants.PLAZAS)
                .whereEqualTo(FirestoreConstants.OCUPADA, false)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Plaza> plazas = new ArrayList<>();
                    for (var doc : snapshot) {
                        plazas.add(new Plaza(doc.getId(), parseTipoPlaza(doc.getString(FirestoreConstants.TIPO_PLAZA))));
                    }
                    callback.onSuccess(plazas);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getResumenPlazasLibresAhora(CallbackWithResult<Map<String, Integer>> callback) {
        db.collection(FirestoreConstants.PLAZAS).get().addOnSuccessListener(plazasSnapshot -> {
            List<Plaza> todasPlazas = new ArrayList<>();
            for (var doc : plazasSnapshot) {
                todasPlazas.add(new Plaza(doc.getId(), parseTipoPlaza(doc.getString(FirestoreConstants.TIPO_PLAZA))));
            }
            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            db.collection(FirestoreConstants.RESERVAS)
                    .whereEqualTo(FirestoreConstants.FECHA, fechaHoy)
                    .get()
                    .addOnSuccessListener(reservasSnapshot -> {
                        List<String> ocupadas = obtenerPlazasOcupadasAhora(reservasSnapshot);
                        Map<String, Integer> resumen = contarPlazasLibresPorTipo(todasPlazas, ocupadas);
                        callback.onSuccess(resumen);
                    })
                    .addOnFailureListener(callback::onFailure);
        }).addOnFailureListener(callback::onFailure);
    }

    private List<String> obtenerPlazasOcupadasAhora(QuerySnapshot snapshot) {
        List<String> ocupadas = new ArrayList<>();
        int nowMinutes = getNowMinutes();
        for (QueryDocumentSnapshot doc : snapshot) {
            List<String> horas = safeCastHoras(doc.get(FirestoreConstants.HORA));
            for (String hora : horas) {
                if (isHoraValidaEnRango(hora, nowMinutes)) {
                    agregarPlazaSiNoOcupada(doc, ocupadas);
                }
            }
        }
        return ocupadas;
    }

    public int getNowMinutes() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE);
    }

    public List<String> safeCastHoras(Object horasObj) {
        if (horasObj instanceof List<?> rawList) {
            List<String> result = new ArrayList<>();
            for (Object item : rawList) {
                if (item instanceof String str) result.add(str);
            }
            return result;
        }
        return Collections.emptyList();
    }

    public boolean isHoraValidaEnRango(String hora, int nowMinutes) {
        String[] parts = hora.split(":");
        if (parts.length != 2) return false;
        int total = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
        int diff = total - nowMinutes;
        return diff >= 0 && diff < 30;
    }

    private void agregarPlazaSiNoOcupada(QueryDocumentSnapshot doc, List<String> ocupadas) {
        String plaza = (String) doc.get(FirestoreConstants.PLAZA);
        if (plaza != null && !ocupadas.contains(plaza)) ocupadas.add(plaza);
    }

    public Map<String, Integer> contarPlazasLibresPorTipo(List<Plaza> plazas, List<String> ocupadas) {
        Map<String, Integer> resumen = new HashMap<>();
        for (Plaza p : plazas) {
            if (!ocupadas.contains(p.getCodigo())) {
                String tipo = p.getTipoPlaza().name();
                resumen.compute(tipo, (k, v) -> v == null ? 1 : v + 1);
            }
        }
        return resumen;
    }



    public void comprobarYCrearReserva(Context context, String fecha, List<String> horas, String tipoPlaza, CallbackWithReserva callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onFailure();
            return;
        }
        db.collection(FirestoreConstants.PLAZAS)
                .whereEqualTo(FirestoreConstants.TIPO_PLAZA, tipoPlaza)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> codigos = new ArrayList<>();
                    snapshot.forEach(doc -> codigos.add(doc.getId()));
                    ReservaRequest request = new ReservaRequest(fecha, horas, tipoPlaza);
                    buscarPlazaLibre(context, codigos, 0, request, callback, user);
                })
                .addOnFailureListener(e -> callback.onFailure());
    }

    private void buscarPlazaLibre(Context context, List<String> codigos, int index, ReservaRequest request, CallbackWithReserva callback, FirebaseUser user) {
        if (index >= codigos.size()) {
            callback.onFailure();
            return;
        }

        String codigo = codigos.get(index);
        db.collection(FirestoreConstants.RESERVAS)
                .whereEqualTo(FirestoreConstants.FECHA, request.fecha())
                .whereEqualTo(FirestoreConstants.PLAZA, codigo)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (existeSolapamiento(snapshot, request.horas())) {
                        buscarPlazaLibre(context, codigos, index + 1, request, callback, user);
                    } else {
                        Reserva nueva = new Reserva(
                                null,
                                parseFecha(request.fecha()),
                                Objects.requireNonNull(user.getEmail()),
                                user.getUid(),
                                new Plaza(codigo, parseTipoPlaza(request.tipoPlaza())),
                                new Hora(request.horas())
                        );
                        guardarReserva(context, nueva, callback);
                    }
                });
    }

    private void guardarReserva(Context context, Reserva reserva, CallbackWithReserva callback) {
        Map<String, Object> reservaData = Map.of(
                FirestoreConstants.FECHA, reserva.getFecha().toDate(),
                FirestoreConstants.USUARIO, reserva.getUsuario(),
                FirestoreConstants.UUID, reserva.getUuid(),
                FirestoreConstants.PLAZA, reserva.getPlaza().getCodigo(),
                FirestoreConstants.HORA, reserva.getHora().getHoras(),
                FirestoreConstants.TIPO_PLAZA, reserva.getPlaza().getTipoPlaza().name()
        );

        db.collection(FirestoreConstants.RESERVAS).add(reservaData)
                .addOnSuccessListener(r -> {
                    String id = r.getId();
                    reserva.setIdReserva(id);
                    db.collection(FirestoreConstants.RESERVAS).document(id)
                            .update(FirestoreConstants.ID_RESERVA, id);
                    db.collection(FirestoreConstants.PLAZAS).document(reserva.getPlaza().getCodigo())
                            .update(FirestoreConstants.OCUPADA, true)
                            .addOnSuccessListener(aVoid -> {
                                NotificationHelper.programarNotificaciones(context, reserva);
                                callback.onSuccess(reserva);
                            })
                            .addOnFailureListener(e -> callback.onFailure());
                })
                .addOnFailureListener(e -> callback.onFailure());
    }

    private boolean existeSolapamiento(QuerySnapshot snapshot, List<String> horasSolicitadas) {
        for (var doc : snapshot) {
            List<String> ocupadas = safeCastHoras(doc.get(FirestoreConstants.HORA));
            for (String h : ocupadas) {
                if (horasSolicitadas.contains(h)) return true;
            }
        }
        return false;
    }

    public void borrarReservaYLiberarPlaza(String reservaId, String plazaId, Callback callback) {
        db.collection(FirestoreConstants.RESERVAS).document(reservaId).delete()
                .addOnSuccessListener(aVoid -> db.collection(FirestoreConstants.PLAZAS).document(plazaId)
                        .update(FirestoreConstants.OCUPADA, false)
                        .addOnSuccessListener(unused -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure()))
                .addOnFailureListener(e -> callback.onFailure());
    }

    public void uploadProfileImage(String uid, Uri imageUri, CallbackWithResult<String> callback) {
        if (imageUri == null) {
            callback.onFailure(new IllegalArgumentException("URI nula"));
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child(FirestoreConstants.PROFILE_IMAGES + uid + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String url = uri.toString();
                    db.collection(FirestoreConstants.USERS).document(uid)
                            .update(FirestoreConstants.IMAGE_URL, url)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(url))
                            .addOnFailureListener(callback::onFailure);
                }).addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }


    public Timestamp parseFecha(Object fecha) {
        if (fecha instanceof Timestamp timestamp) return timestamp;
        if (fecha instanceof String fechaStr) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                return new Timestamp(Objects.requireNonNull(sdf.parse(fechaStr)));
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + fechaStr, e);
            }
        }
        return null;
    }

    public PlazaType parseTipoPlaza(String str) {
        if (str == null) return PlazaType.COCHE;
        return switch (str.toLowerCase()) {
            case "electrico" -> PlazaType.ELECTRICO;
            case "discapacitado" -> PlazaType.DISCAPACITADO;
            case "moto" -> PlazaType.MOTO;
            default -> PlazaType.COCHE;
        };
    }

    public String safeString(String value) {
        return value != null ? value : "";
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void getCurrentUserData(String uid, CallbackWithResult<Map<String, Object>> callbackWithResult) {
        db.collection(FirestoreConstants.USERS).document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        if (userData != null) {
                            callbackWithResult.onSuccess(userData);
                        } else {
                            callbackWithResult.onFailure(new NullPointerException("User data is null"));
                        }
                    } else {
                        callbackWithResult.onFailure(new NoSuchElementException("User not found"));
                    }
                })
                .addOnFailureListener(callbackWithResult::onFailure);
    }

    public void updateUserProfile(String uid, String name, String email, String phone, CallbackWithResult<Boolean> callback) {
        Map<String, Object> updates = new HashMap<>();
        if (name != null && !name.isEmpty()) updates.put(FirestoreConstants.NOMBRE, name);
        if (email != null && !email.isEmpty()) updates.put(FirestoreConstants.EMAIL, email);
        if (phone != null && !phone.isEmpty()) updates.put(FirestoreConstants.TELEFONO, phone);

        db.collection(FirestoreConstants.USERS).document(uid).update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess(true))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user profile", e);
                    callback.onFailure(e);
                });
    }

    public void getReservasUsuario(String uid, CallbackWithResult<List<Reserva>> callbackWithResult) {
        db.collection(FirestoreConstants.RESERVAS)
                .whereEqualTo(FirestoreConstants.UUID, uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Reserva> reservas = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String id = doc.getId();
                        Timestamp fecha = parseFecha(doc.get(FirestoreConstants.FECHA));
                        String usuario = (String) doc.get(FirestoreConstants.USUARIO);
                        Plaza plaza = new Plaza((String) doc.get(FirestoreConstants.PLAZA),
                                parseTipoPlaza((String) doc.get(FirestoreConstants.TIPO_PLAZA)));
                        Hora hora = new Hora(safeCastHoras(doc.get(FirestoreConstants.HORA)));
                        reservas.add(new Reserva(id, fecha, Objects.requireNonNull(usuario), uid, plaza, hora));
                    }
                    callbackWithResult.onSuccess(reservas);
                })
                .addOnFailureListener(callbackWithResult::onFailure);
    }

    public void updateHoraReserva(String idReserva, List<String> nuevaHora, Callback callback) {
        db.collection(FirestoreConstants.RESERVAS).document(idReserva)
                .update(FirestoreConstants.HORA, nuevaHora)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating reservation hour", e);
                    callback.onFailure();
                });
    }
    public void listenForVehicles(CallbackWithResult<List<Vehicle>> callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onSuccess(new ArrayList<>());
            return;
        }

        vehiclesListener = db.collection(FirestoreConstants.USERS)
                .document(user.getUid())
                .collection(FirestoreConstants.VEHICULOS)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        callback.onFailure(new Exception("Error al escuchar vehículos."));
                        return;
                    }

                    List<Vehicle> list = new ArrayList<>();
                    for (var doc : value) {
                        Vehicle vehicle = doc.toObject(Vehicle.class);
                            vehicle.setId(doc.getId());
                            list.add(vehicle);
                    }
                    callback.onSuccess(list);
                });
    }

    public void removeVehiclesListener() {
        if (vehiclesListener != null) vehiclesListener.remove();
    }

    public void addVehiculo(String plate, String pollutionType, String selectedType, Uri imageUri, Callback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onFailure();
            return;
        }

        Map<String, Object> vehicleData = new HashMap<>();
        vehicleData.put("matricula", plate);
        vehicleData.put("contaminacion", pollutionType);
        vehicleData.put("tipo", selectedType);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("vehicle_images/" + plate + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            vehicleData.put("imagenUrl", uri.toString());
                            db.collection("users")
                                    .document(user.getUid())
                                    .collection("vehiculos")
                                    .document(plate)
                                    .set(vehicleData)
                                    .addOnSuccessListener(unused -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure());
                        }).addOnFailureListener(e -> callback.onFailure())
                ).addOnFailureListener(e -> callback.onFailure());
    }

    public void deleteVehiculo(String matricula, Callback callback) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            callback.onFailure();
            return;
        }
        db.collection("users")
                .document(user.getUid())
                .collection("vehiculos")
                .document(matricula)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure());
    }
}
