package com.lksnext.parkingplantilla.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackWithReserva;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.Plaza;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {

    private final DataRepository repository = DataRepository.getInstance();
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<List<Plaza>> plazasLibres = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> resumenPlazas = new MutableLiveData<>();

    public LiveData<Map<String, Integer>> getResumenPlazas() {
        return resumenPlazas;
    }

    public void cargarResumenPlazas() {
        repository.getPlazasLibres(new CallbackWithResult<List<Plaza>>() {
            @Override
            public void onSuccess(List<Plaza> plazas) {
                Map<String, Integer> resumen = new HashMap<>();
                for (Plaza p : plazas) {
                    String tipo = p.getTipoPlaza().name(); // o p.getTipoPlaza().toString()
                    int count = resumen.containsKey(tipo) ? resumen.get(tipo) : 0;
                    resumen.put(tipo, count + 1);
                }
                resumenPlazas.postValue(resumen);
            }

            @Override
            public void onFailure(Exception e) {
                resumenPlazas.postValue(null);
            }
        });
    }
    public void cargarResumenPlazasAhora() {
        repository.getResumenPlazasLibresAhora(new CallbackWithResult<Map<String, Integer>>() {
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


    public void setUserName(String name) {
        userName.setValue(name);
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<List<Plaza>> getPlazasLibres() {
        return plazasLibres;
    }

    public void cargarPlazasLibres() {
        repository.getPlazasLibres(new CallbackWithResult<List<Plaza>>() {
            @Override
            public void onSuccess(List<Plaza> plazas) {
                plazasLibres.postValue(plazas);
            }

            @Override
            public void onFailure(Exception e) {
                plazasLibres.postValue(null); // O manejar un estado de error
            }
        });
    }

    public void reservarSiLibre(Context context, String fecha, List<String> horas, String tipoPlaza, CallbackWithReserva callback) {
        repository.comprobarYCrearReserva(context, fecha, horas, tipoPlaza, callback);
    }

}
