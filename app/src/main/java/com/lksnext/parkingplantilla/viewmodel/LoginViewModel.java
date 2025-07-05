package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class LoginViewModel extends ViewModel {

    MutableLiveData<Boolean> logged = new MutableLiveData<>(null);
    private MutableLiveData<String> userName = new MutableLiveData<>();

    public LiveData<Boolean> isLogged(){
        return logged;
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        userName.setValue(name);
    }

    public void loginUser(String email, String password) {
        DataRepository.getInstance().login(email, password, new Callback() {
            @Override
            public void onSuccess() {
                logged.setValue(Boolean.TRUE);
                userName.setValue(email);
            }
            @Override
            public void onFailure() {
                logged.setValue(Boolean.FALSE);
                userName.setValue(null);
            }
        });
    }


    public void loginWithGoogle(AuthCredential credential) {
        DataRepository.getInstance().loginWithCredential(credential, new Callback() {
            @Override
            public void onSuccess() {
                logged.setValue(Boolean.TRUE);

                FirebaseUser user = DataRepository.getInstance().getCurrentUser();
                if (user != null) {
                    userName.setValue(user.getEmail());

                }
            }
            @Override
            public void onFailure() {
                logged.setValue(Boolean.FALSE);
                userName.setValue(null);
            }
        });
    }



}