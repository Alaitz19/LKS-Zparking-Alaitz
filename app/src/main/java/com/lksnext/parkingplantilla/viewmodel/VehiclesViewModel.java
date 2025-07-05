package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.Vehicle;

import java.util.List;

public class VehiclesViewModel extends ViewModel {

    private final DataRepository repository;
    private final MutableLiveData<List<Vehicle>> vehicles = new MutableLiveData<>();

    public VehiclesViewModel(DataRepository repository) {
        this.repository = repository;
        listenForVehicles();
    }

    public LiveData<List<Vehicle>> getVehicles() {
        return vehicles;
    }

    private void listenForVehicles() {
        repository.listenForVehicles(new CallbackWithResult<>() {
            @Override
            public void onSuccess(List<Vehicle> result) {
                vehicles.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                vehicles.postValue(null);
            }
        });
    }


    public void addVehiculo(String plate, String pollutionType, String selectedType,
                            android.net.Uri imageUri, Callback callback) {
        repository.addVehiculo(plate, pollutionType, selectedType, imageUri, callback);
    }

    public void deleteVehiculo(String matricula, Callback callback) {
        repository.deleteVehiculo(matricula, callback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.removeVehiclesListener();
    }
}
