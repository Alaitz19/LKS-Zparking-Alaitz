package com.lksnext.parkingplantilla.domain;

public interface CallbackWithReserva {
    void onSuccess(Reserva reserva);
    void onFailure();

}
