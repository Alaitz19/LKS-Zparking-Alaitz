package com.lksnext.parkingplantilla.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;

import java.util.Map;

public class ProfileViewModel extends AndroidViewModel {

    private final DataRepository repo;
    private final MutableLiveData<Map<String, Object>> userData = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application, DataRepository repository) {
        super(application);
        this.repo = repository;
        loadUserData();
    }

    public void loadUserData() {
        FirebaseUser user = repo.getCurrentUser();
        if (user != null) {
            repo.getCurrentUserData(user.getUid(), new CallbackWithResult<>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    userData.postValue(result);
                }

                @Override
                public void onFailure(Exception e) {
                    userData.postValue(null);
                }
            });
        } else {
            userData.postValue(null);
        }
    }

    public MutableLiveData<Map<String, Object>> getUserData() {
        return userData;
    }

    public void logout() {
        repo.logout();
    }

    public void updateUserProfile(String name, String email, String phone, CallbackWithResult<Boolean> callback) {
        FirebaseUser user = repo.getCurrentUser();
        if (user != null) {
            repo.updateUserProfile(user.getUid(), name, email, phone, callback);
        } else {
            callback.onFailure(new Exception("No user logged in"));
        }
    }

    public void uploadProfileImage(Uri selectedImageUri, CallbackWithResult<String> callback) {
        FirebaseUser user = repo.getCurrentUser();
        if (user != null) {
            repo.uploadProfileImage(user.getUid(), selectedImageUri, callback);
        } else {
            callback.onFailure(new Exception("No user logged in"));
        }
    }
}
