package com.lksnext.parkingplantilla.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.viewmodel.VehiclesViewModel;

public class VehiclesViewModelFactory implements ViewModelProvider.Factory {

    private final DataRepository repository;

    public VehiclesViewModelFactory(DataRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(VehiclesViewModel.class)) {
            return (T) new VehiclesViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
