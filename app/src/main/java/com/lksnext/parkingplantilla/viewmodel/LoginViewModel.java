package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class LoginViewModel extends ViewModel {

    private final DataRepository repository;

    private final MutableLiveData<Boolean> logged = new MutableLiveData<>(null);
    private final MutableLiveData<String> userName = new MutableLiveData<>(null);

    // ✅ Constructor inyectable
    public LoginViewModel(DataRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> isLogged() {
        return logged;
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public void loginUser(String email, String password) {
        repository.login(email, password, new Callback() {
            @Override
            public void onSuccess() {
                logged.postValue(Boolean.TRUE);
                userName.postValue(email);
            }

            @Override
            public void onFailure() {
                logged.postValue(Boolean.FALSE);
                userName.postValue(null);
            }
        });
    }

    public void loginWithGoogle(AuthCredential credential) {
        repository.loginWithCredential(credential, new Callback() {
            @Override
            public void onSuccess() {
                logged.postValue(Boolean.TRUE);
                FirebaseUser user = repository.getCurrentUser();
                userName.postValue(user != null ? user.getEmail() : null);
            }

            @Override
            public void onFailure() {
                logged.postValue(Boolean.FALSE);
                userName.postValue(null);
            }
        });
    }
}
