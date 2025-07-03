package com.lksnext.parkingplantilla.domain;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.google.firebase.Timestamp;

import java.util.Objects;

public class Reserva {
    private String idReserva;

    private Timestamp fecha;
    private String usuario;
    private String uuid;
    private Plaza plaza;
    private Hora hora;

    public Reserva() {
    }

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

    public void setFecha(@NonNull Timestamp fecha) {
        this.fecha = fecha;
    }

    @NonNull
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(@NonNull String usuario) {
        this.usuario = usuario;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    @NonNull
    public Plaza getPlaza() {
        return plaza;
    }

    public void setPlaza(@NonNull Plaza plaza) {
        this.plaza = plaza;
    }

    @NonNull
    public Hora getHora() {
        return hora;
    }

    public void setHora(@NonNull Hora hora) {
        this.hora = hora;
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

    public static final DiffUtil.ItemCallback<Reserva> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Reserva oldReserva, @NonNull Reserva newReserva) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return Objects.equals(oldReserva.getUuid(), newReserva.getUuid());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Reserva oldItem, @NonNull Reserva newItem) {
                    return oldItem.equals(newItem);
                }
            };



    public String remainingTime() {
        if (hora == null || hora.getHoras().isEmpty() || fecha == null) {
            return "Sin reserva";
        }

        try {
            // Ejemplo: fecha: 2025-07-03, hora fin: "12:00"
            String fechaStr = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(fecha.toDate());

            String horaFinStr = hora.getHoras().get(hora.getHoras().size() - 1); // última hora como fin

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
            java.util.Date endDate = sdf.parse(fechaStr + " " + horaFinStr);

            if (endDate == null) {
                return "Formato inválido";
            }

            long millisRemaining = endDate.getTime() - System.currentTimeMillis();

            if (millisRemaining <= 0) {
                return "Reserva terminada";
            }

            long seconds = millisRemaining / 1000;
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;

            return String.format("%02d:%02d:%02d", hours, minutes, secs);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error tiempo";
        }
    }

    public Object getId() {
        return uuid;
    }

    public void borrarReserva(Context context, Callback callback) {

    }


}