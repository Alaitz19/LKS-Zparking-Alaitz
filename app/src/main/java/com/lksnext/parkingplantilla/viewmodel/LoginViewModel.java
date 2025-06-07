package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class LoginViewModel extends ViewModel {

    // Aquí puedes declarar los LiveData y métodos necesarios para la vista de inicio de sesión
    MutableLiveData<Boolean> logged = new MutableLiveData<>(null);

    public LiveData<Boolean> isLogged(){
        return logged;
    }

    public void loginUser(String email, String password) {
        //Clase para comprobar si los datos de inicio de sesión son correctos o no
        DataRepository.getInstance().login(email, password, new Callback() {
            //En caso de que el login sea correcto, que se hace
            @Override
            public void onSuccess() {
                //TODO
                logged.setValue(Boolean.TRUE);
                userName.setValue(email); // Asignar el nombre de usuario
            }

            //En caso de que el login sea incorrecto, que se hace
            @Override
            public void onFailure() {
                //TODO
                logged.setValue(Boolean.FALSE);
                userName.setValue(null); // Limpiar el nombre de usuario
            }
        });
    }
    private MutableLiveData<String> userName = new MutableLiveData<>();
    public LiveData<String> getUserName() {
        return userName;
    }
}

