package com.lksnext.parkingplantilla.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;


import com.lksnext.parkingplantilla.domain.Callback;

import java.util.List;

public class MainViewModel extends ViewModel {
    // Aquí puedes declarar los LiveData y métodos necesarios para la vista main
    private final DataRepository repository = DataRepository.getInstance();
    private final MutableLiveData<String> userName = new MutableLiveData<>();


    public void setUserName(String name) {
        userName.setValue(name);
    }

    public LiveData<String> getUserName() {
        return userName;
    }



    public void reservarSiLibre(Context context, String fecha, List<String> horas, String tipoPlaza, Callback callback) {
        repository.comprobarYCrearReserva(context, fecha, horas, tipoPlaza, callback);
    }



}
