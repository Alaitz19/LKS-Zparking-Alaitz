package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.CallbackWithReserva;
import com.lksnext.parkingplantilla.domain.Plaza;

import android.content.Context;

import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {

    private final DataRepository repository;
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<List<Plaza>> plazasLibres = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> resumenPlazas = new MutableLiveData<>();

    public MainViewModel(DataRepository repository) {
        this.repository = repository;
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<List<Plaza>> getPlazasLibres() {
        return plazasLibres;
    }

    public LiveData<Map<String, Integer>> getResumenPlazas() {
        return resumenPlazas;
    }

    public void cargarUserName() {
        String name = repository.getUserName();
        if (name != null && !name.isEmpty()) {
            userName.postValue(name);
        } else {
            userName.postValue("Usuario");
        }
    }

    public void cargarPlazasLibres() {
        repository.getPlazasLibres(new CallbackWithResult<>() {
            @Override
            public void onSuccess(List<Plaza> plazas) {
                plazasLibres.postValue(plazas);
            }

            @Override
            public void onFailure(Exception e) {
                plazasLibres.postValue(null);
            }
        });
    }

    public void cargarResumenPlazasAhora() {
        repository.getResumenPlazasLibresAhora(new CallbackWithResult<>() {
            @Override
            public void onSuccess(Map<String, Integer> result) {
                resumenPlazas.postValue(result);
            }

            @Override
            public void onFailure(Exception e) {
                resumenPlazas.postValue(null);
            }
        });
    }

    public void reservarSiLibre(Context context, String fecha, List<String> horas, String tipoPlaza, CallbackWithReserva callback) {
        repository.comprobarYCrearReserva(context, fecha, horas, tipoPlaza, callback);
    }
}
