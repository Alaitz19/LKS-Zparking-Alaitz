package com.lksnext.parkingplantilla.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;

import java.util.Map;

public class ProfileViewModel extends AndroidViewModel {

    private final DataRepository repo;
    private final MutableLiveData<Map<String, Object>> userData = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        repo = DataRepository.getInstance();
        loadUserData();
    }

    public void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            repo.getCurrentUserData(user.getUid(), new CallbackWithResult<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    userData.postValue(result);
                }

                @Override
                public void onFailure(Exception e) {
                    userData.postValue(null);
                }
            });
        }
    }

    public MutableLiveData<Map<String, Object>> getUserData() {
        return userData;
    }

    public void logout() {
        repo.logout();
    }

    public boolean isLoggedIn() {
        return repo.getCurrentUser() != null;
    }

    public void updateUserProfile(String name, String email, String phone, CallbackWithResult<Boolean> callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            repo.updateUserProfile(user.getUid(), name, email, phone, new CallbackWithResult<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    callback.onSuccess(result);
                }

                @Override
                public void onFailure(Exception e) {
                    callback.onFailure(e);
                }
            });
        } else {
            callback.onFailure(new Exception("No user logged in"));
        }
    }

    public void uploadProfileImage(Uri selectedImageUri, CallbackWithResult<String> imagenActualizadaCorrectamente) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            repo.uploadProfileImage(user.getUid(), selectedImageUri, new CallbackWithResult<String>() {
                @Override
                public void onSuccess(String imageUrl) {
                    imagenActualizadaCorrectamente.onSuccess(imageUrl);
                }

                @Override
                public void onFailure(Exception e) {
                    imagenActualizadaCorrectamente.onFailure(e);
                }
            });
        } else {
            imagenActualizadaCorrectamente.onFailure(new Exception("No user logged in"));
        }
    }
}
