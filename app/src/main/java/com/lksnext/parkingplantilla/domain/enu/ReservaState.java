package com.lksnext.parkingplantilla.domain.enu;

public enum ReservaState {
    PENDIENTE("Pendiente"),
    ACEPTADA("Aceptada"),
    RECHAZADA("Rechazada"),
    CANCELADA("Cancelada"),
    MODIFICADA("Modificada");

    private final String state;

    ReservaState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return state;
    }
}
