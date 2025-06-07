package com.lksnext.parkingplantilla.domain;

import java.util.List;

public class Plaza {

    long id;
    String tipo;

    double lat;
    double lon;

    public Plaza() {

    }

    public Plaza(long id, String tipo, double lat, double lon) {
        this.id = id;
        this.tipo = tipo;
        this.lat = lat;
        this.lon = lon;
    }

    public String getTipo() {
        return tipo;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public boolean estaReservada(Plaza plaza, String fecha, Hora horaBusqueda, List<Reserva> reservas) {
        for (Reserva r : reservas) {
            if (r.getPlazaId().getId() == plaza.getId() && r.getFecha().equals(fecha)) {
                Hora h = r.getHoraInicio();
                // Comprobar solapamiento
                if (!(horaBusqueda.getHoraFin() <= h.getHoraInicio() || horaBusqueda.getHoraInicio() >= h.getHoraFin())) {
                    return true;  // hay solapamiento de horas
                }
            }
        }
        return false;  // no está reservada para ese intervalo
    }

}
