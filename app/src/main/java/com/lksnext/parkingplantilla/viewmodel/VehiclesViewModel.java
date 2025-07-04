package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import com.lksnext.parkingplantilla.domain.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehiclesViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final MutableLiveData<List<Vehicle>> vehiclesLiveData = new MutableLiveData<>();
    private ListenerRegistration listenerRegistration;

    public VehiclesViewModel() {
        vehiclesLiveData.setValue(new ArrayList<>());
        startListeningForVehicles();
    }

    public LiveData<List<Vehicle>> getVehicles() {
        return vehiclesLiveData;
    }

    private void startListeningForVehicles() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        listenerRegistration = db.collection("users")
                .document(user.getUid())
                .collection("vehiculos")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        vehiclesLiveData.setValue(new ArrayList<>());
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        List<Vehicle> list = new ArrayList<>();
                        for (DocumentSnapshot doc : value) {
                            Vehicle vehicle = doc.toObject(Vehicle.class);
                            if (vehicle != null) {
                                vehicle.setId(doc.getId());
                                list.add(vehicle);
                            }
                        }
                        vehiclesLiveData.setValue(list);
                    } else {
                        vehiclesLiveData.setValue(new ArrayList<>());
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerRegistration != null) listenerRegistration.remove();
    }
}
