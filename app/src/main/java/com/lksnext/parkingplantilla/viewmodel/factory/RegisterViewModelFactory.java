package com.lksnext.parkingplantilla.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.viewmodel.RegisterViewModel;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {

    private final DataRepository repository;

    public RegisterViewModelFactory(DataRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
