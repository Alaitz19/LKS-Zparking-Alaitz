package com.lksnext.parkingplantilla.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final DataRepository repository;

    public MainViewModelFactory(DataRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
