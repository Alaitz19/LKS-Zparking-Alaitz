package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;

import java.util.ArrayList;
import java.util.List;

public class ReservationsViewModel extends ViewModel {
    private final MutableLiveData<List<Reserva>> reservas = new MutableLiveData<>();
    private final DataRepository repo = DataRepository.getInstance();

    public LiveData<List<Reserva>> getReservas() { return reservas; }

    public void cargarReservas(String uid) {
        repo.getReservasUsuario(uid, new CallbackWithResult<List<Reserva>>() {
            @Override
            public void onSuccess(List<Reserva> result) {
                reservas.postValue(result);
            }
            @Override
            public void onFailure(Exception e) {
                reservas.postValue(new ArrayList<>());
            }
        });
    }
    // En ReservationsViewModel.java
    public void borrarReserva(String reservaId, String plazaId, Callback callback) {
        repo.borrarReservaYLiberarPlaza(reservaId, plazaId, new Callback() {
            @Override
            public void onSuccess() {
                List<Reserva> currentReservas = reservas.getValue();
                if (currentReservas != null) {
                    currentReservas.removeIf(r -> r.getIdReserva().equals(reservaId));
                    reservas.postValue(currentReservas);
                }
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }



}