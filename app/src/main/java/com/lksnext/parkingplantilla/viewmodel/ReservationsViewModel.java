package com.lksnext.parkingplantilla.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.receiver.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationsViewModel extends ViewModel {

    private final MutableLiveData<List<Reserva>> reservas = new MutableLiveData<>(new ArrayList<>());
    private List<Reserva> todasLasReservas = new ArrayList<>();

    private final DataRepository repo;

    public ReservationsViewModel(DataRepository repo) {
        this.repo = repo;
    }

    public LiveData<List<Reserva>> getReservas() {
        return reservas;
    }

    public void cargarReservas(String uid) {
        repo.getReservasUsuario(uid, new CallbackWithResult<List<Reserva>>() {
            @Override
            public void onSuccess(List<Reserva> result) {
                todasLasReservas = result;
                reservas.postValue(new ArrayList<>(result));
            }

            @Override
            public void onFailure(Exception e) {
                reservas.postValue(new ArrayList<>());
            }
        });
    }

    public void borrarReserva(String reservaId, String plazaId, Callback callback) {
        repo.borrarReservaYLiberarPlaza(reservaId, plazaId, new Callback() {
            @Override
            public void onSuccess() {
                todasLasReservas.removeIf(r -> reservaId.equals(r.getIdReserva()));
                reservas.postValue(new ArrayList<>(todasLasReservas));
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public void editarReservaHora(Reserva reserva, List<String> nuevaHora, Callback callback) {
        repo.updateHoraReserva(reserva.getIdReserva(), nuevaHora, new Callback() {
            @Override
            public void onSuccess() {
                for (Reserva r : todasLasReservas) {
                    if (r.getIdReserva().equals(reserva.getIdReserva())) {
                        r.getHora().setHoras(nuevaHora);
                        break;
                    }
                }
                reservas.postValue(new ArrayList<>(todasLasReservas));
                callback.onSuccess();
            }

            @Override
            public void onFailure() {
                callback.onFailure();
            }
        });
    }

    public void filtrarTerminadas() {
        List<Reserva> filtradas = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Reserva reserva : todasLasReservas) {
            if (calcularFinMillis(reserva) < now) {
                filtradas.add(reserva);
            }
        }
        reservas.postValue(filtradas);
    }

    public void filtrarSiguientes() {
        List<Reserva> filtradas = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Reserva reserva : todasLasReservas) {
            if (calcularInicioMillis(reserva) > now) {
                filtradas.add(reserva);
            }
        }
        reservas.postValue(filtradas);
    }

    public void filtrarEnCurso() {
        List<Reserva> filtradas = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Reserva reserva : todasLasReservas) {
            long start = calcularInicioMillis(reserva);
            long end = calcularFinMillis(reserva);
            if (start <= now && end >= now) {
                filtradas.add(reserva);
            }
        }
        reservas.postValue(filtradas);
    }

    private long calcularInicioMillis(Reserva reserva) {
        return parseFechaHoraMillis(reserva.getFecha().toDate(), reserva.getHora().getHoraInicio());
    }

    private long calcularFinMillis(Reserva reserva) {
        return parseFechaHoraMillis(reserva.getFecha().toDate(), reserva.getHora().getHoraFin());
    }

    private long parseFechaHoraMillis(Date fecha, String hora) {
        try {
            String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(fecha);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(fechaStr + " " + hora);
            return (date != null) ? date.getTime() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    public void programarNotificaciones(Context context, Reserva reserva) {
        NotificationHelper.programarNotificaciones(context, reserva);
    }

}
