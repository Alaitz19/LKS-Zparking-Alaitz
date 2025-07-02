package com.lksnext.parkingplantilla.domain.enu;

public enum PlazaType {
    COCHE("Coche"),
    DISCAPACITADO("Discapacitado"),
    ELECTRICO("Eléctrico"),
    MOTO("Moto");

    private final String type;

    PlazaType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }
}
