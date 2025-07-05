package com.lksnext.parkingplantilla.domain;



import androidx.annotation.NonNull;
import com.google.firebase.Timestamp;
import java.util.Objects;

public class Reserva {
    private String idReserva;

    private final Timestamp fecha;
    private final String usuario;
    private final String uuid;
    private final Plaza plaza;
    private final Hora hora;

    public Reserva( String idReserva, Timestamp fecha, @NonNull String usuario, @NonNull String uuid, @NonNull Plaza plaza, @NonNull Hora hora) {
        this.idReserva = idReserva;
        this.fecha = fecha;
        this.usuario = usuario;
        this.uuid = uuid;
        this.plaza = plaza;
        this.hora = hora;
    }
    public String getIdReserva() { return idReserva; }

    public Timestamp getFecha() {
        return fecha;
    }


    @NonNull
    public String getUsuario() {
        return usuario;
    }


    @NonNull
    public String getUuid() {
        return uuid;
    }

    @NonNull
    public Plaza getPlaza() {
        return plaza;
    }



    @NonNull
    public Hora getHora() {
        return hora;
    }


    public void setIdReserva(String id) {
        this.idReserva = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(fecha, reserva.fecha) && Objects.equals(usuario, reserva.usuario) && Objects.equals(uuid, reserva.uuid) && Objects.equals(plaza, reserva.plaza) && Objects.equals(hora, reserva.hora);
    }



    @Override
    public int hashCode() {
        return Objects.hash(fecha, usuario, uuid, plaza, hora);
    }

    @NonNull
    @Override
    public String toString() {
        return "Reserva{" +
                "fecha=" + fecha +
                ", usuario='" + usuario + '\'' +
                ", uuid='" + uuid + '\'' +
                ", plaza=" + plaza +
                ", hora=" + hora +
                '}';
    }

}