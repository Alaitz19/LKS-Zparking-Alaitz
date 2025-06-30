package com.lksnext.parkingplantilla.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import com.lksnext.parkingplantilla.data.AuthRepository;

public class ProfileViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public ProfileViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public void logout() {
        authRepository.logout();
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }
}