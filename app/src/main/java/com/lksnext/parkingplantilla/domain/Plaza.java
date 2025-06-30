package com.lksnext.parkingplantilla.domain;
import androidx.annotation.NonNull;


import com.lksnext.parkingplantilla.domain.enu.PlazaType;

import java.util.Objects;

public class Plaza {
    private long id;
    private PlazaType tipoPlaza;

    public Plaza(){

    }

    public Plaza(long id, PlazaType tipoPlaza) {
        this.id = id;
        this.tipoPlaza = tipoPlaza;
    }

    public long getId() {
        return id;
    }

    public PlazaType getTipoPlaza() {
        return tipoPlaza;
    }

    public void setTipoPlaza(PlazaType tipoPlaza) {
        this.tipoPlaza = tipoPlaza;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plaza plaza = (Plaza) o;
        return id == plaza.id && tipoPlaza == plaza.tipoPlaza;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tipoPlaza);
    }

    @NonNull
    @Override
    public String toString() {
        return "Plaza{" +
                "id=" + id +
                ", tipoPlaza=" + tipoPlaza +
                '}';
    }
}