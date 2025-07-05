package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class RegisterViewModel extends ViewModel {

    private final DataRepository repo;

    public RegisterViewModel(DataRepository repo) {
        this.repo = repo;
    }

    public void register(String email, String password, String username, String phone, Callback callback) {
        repo.register(email, password, phone, callback);
    }
}
