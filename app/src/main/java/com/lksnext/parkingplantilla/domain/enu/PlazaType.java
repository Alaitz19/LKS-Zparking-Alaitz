package com.lksnext.parkingplantilla.domain.enu;

import androidx.annotation.NonNull;

public enum PlazaType {
    COCHE("Coche"),
    DISCAPACITADO("Discapacitado"),
    ELECTRICO("Electrico"),
    MOTO("Moto");

    private final String type;

    PlazaType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }
}
