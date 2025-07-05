package com.lksnext.parkingplantilla.viewmodel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.viewmodel.ReservationsViewModel;

public class ReservationsViewModelFactory implements ViewModelProvider.Factory {

    private final DataRepository repository;

    public ReservationsViewModelFactory(DataRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReservationsViewModel.class)) {
            return (T) new ReservationsViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
