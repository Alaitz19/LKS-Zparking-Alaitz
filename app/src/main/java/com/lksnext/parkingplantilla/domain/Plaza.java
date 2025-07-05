package com.lksnext.parkingplantilla.domain;
import androidx.annotation.NonNull;


import com.lksnext.parkingplantilla.domain.enu.PlazaType;

import java.util.Objects;

public class Plaza {
    private String codigo;
    private boolean ocupada;
    private PlazaType tipoPlaza;

    private String direccion;

    public Plaza() {
    }

    public Plaza(String codigo, boolean ocupada, PlazaType tipoPlaza) {
        this.codigo = codigo;
        this.ocupada = ocupada;
        this.tipoPlaza = tipoPlaza;
    }

    public Plaza(String idPlaza, PlazaType tipoPlaza) {
        this.codigo = idPlaza;
        this.ocupada = false;
        this.tipoPlaza = tipoPlaza;
    }

    public String getCodigo() {
        return codigo;
    }


    public PlazaType getTipoPlaza() {
        return tipoPlaza;
    }
    public String getTipo() {
        return tipoPlaza != null ? tipoPlaza.getType() : "Desconocido";
    }

    public String getDireccion() {

        return direccion != null ? direccion : "Dirección no disponible";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plaza plaza)) return false;
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