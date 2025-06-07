package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    // Aquí puedes declarar los LiveData y métodos necesarios para la vista main

    private final MutableLiveData<String> userName = new MutableLiveData<>();

    public void setUserName(String name) {
        userName.setValue(name);
    }

    public LiveData<String> getUserName() {
        return userName;
    }
}
