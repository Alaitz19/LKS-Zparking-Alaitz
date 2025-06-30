package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.ViewModel;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class RegisterViewModel extends ViewModel {
    public void register(String email, String password, String username, String phone, Callback callback) {
        DataRepository.getInstance().register(email, password, phone, callback);
    }

}