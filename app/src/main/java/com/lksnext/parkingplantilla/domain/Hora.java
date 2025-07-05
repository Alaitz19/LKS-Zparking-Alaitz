package com.lksnext.parkingplantilla.domain;

import java.util.List;

public class Hora {

    public List<String> getHoras() {
        return horas;
    }

    private List<String> horas;

    public Hora(List<String> horas) {
        this.horas = horas;
    }

    public String getHoraInicio() {
        return (horas != null && !horas.isEmpty()) ? horas.get(0) : "";
    }

    public String getHoraFin() {
        return (horas != null && !horas.isEmpty()) ? horas.get(horas.size() - 1) : "";
    }


    public void setHoras(List<String> nuevaHora){

        this.horas = nuevaHora;

    }
}


