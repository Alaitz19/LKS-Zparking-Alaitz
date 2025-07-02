package com.lksnext.parkingplantilla.domain;
import androidx.annotation.NonNull;


import com.lksnext.parkingplantilla.domain.enu.PlazaType;

import java.util.Objects;

public class Plaza {
    private String codigo;        // antes era long id, ahora String código
    private boolean ocupada;      // nuevo campo booleano para ocupada
    private PlazaType tipoPlaza;

    public Plaza() {
    }

    public Plaza(String codigo, boolean ocupada, PlazaType tipoPlaza) {
        this.codigo = codigo;
        this.ocupada = ocupada;
        this.tipoPlaza = tipoPlaza;
    }

    public Plaza(String idPlaza, PlazaType tipoPlaza) {
        this.codigo = idPlaza;
        this.ocupada = false; // Por defecto, una plaza nueva no está ocupada
        this.tipoPlaza = tipoPlaza;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public boolean isOcupada() {
        return ocupada;
    }

    public void setOcupada(boolean ocupada) {
        this.ocupada = ocupada;
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
        if (!(o instanceof Plaza)) return false;
        Plaza plaza = (Plaza) o;
        return ocupada == plaza.ocupada &&
                Objects.equals(codigo, plaza.codigo) &&
                tipoPlaza == plaza.tipoPlaza;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo, ocupada, tipoPlaza);
    }

    @NonNull
    @Override
    public String toString() {
        return "Plaza{" +
                "codigo='" + codigo + '\'' +
                ", ocupada=" + ocupada +
                ", tipoPlaza=" + tipoPlaza +
                '}';
    }


}