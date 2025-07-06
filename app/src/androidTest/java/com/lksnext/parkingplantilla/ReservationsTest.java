package com.lksnext.parkingplantilla;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


import com.google.firebase.Timestamp;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.domain.enu.PlazaType;

import com.lksnext.parkingplantilla.viewmodel.ReservationsViewModel;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReservationsTest {

    private DataRepository mockRepo;
    private ReservationsViewModel viewModel;

    @Before
    public void setup() {
        mockRepo = mock(DataRepository.class);
        viewModel = new ReservationsViewModel(mockRepo);
    }

    @Test
    public void testBorrarReserva_actualizaListaYCallbackOk() {
        // Reserva realista
        Plaza plaza = new Plaza("COC001", true, PlazaType.COCHE);
        Hora hora = new Hora(Arrays.asList("10:00", "10:30"));
        Reserva reserva = new Reserva("reserva123", Timestamp.now(), "user1", "uuid1", plaza, hora);

        viewModel.todasLasReservas.add(reserva);

        Callback callback = mock(Callback.class);

        // Simula éxito en repo
        doAnswer(invocation -> {
            Callback repoCallback = invocation.getArgument(2);
            repoCallback.onSuccess();
            return null;
        }).when(mockRepo).borrarReservaYLiberarPlaza(eq("reserva123"), eq("COC001"), any());

        // Ejecuta
        viewModel.borrarReserva("reserva123", "COC001", callback);

        assertTrue(viewModel.todasLasReservas.isEmpty());
        assertTrue(viewModel.getReservas().getValue().isEmpty());
        verify(callback).onSuccess();
    }

    @Test
    public void testEditarReservaHora_actualizaHorasYCallbackOk() {
        Plaza plaza = new Plaza("COC001", true, PlazaType.COCHE);
        Hora hora = new Hora(new ArrayList<>(List.of("10:00", "10:30")));
        Reserva reserva = new Reserva("reserva123", Timestamp.now(), "user1", "uuid1", plaza, hora);

        viewModel.todasLasReservas.add(reserva);

        List<String> nuevasHoras = List.of("11:00", "11:30");
        Callback callback = mock(Callback.class);

        // Simula éxito
        doAnswer(invocation -> {
            Callback repoCallback = invocation.getArgument(2);
            repoCallback.onSuccess();
            return null;
        }).when(mockRepo).updateHoraReserva(eq("reserva123"), eq(nuevasHoras), any());

        viewModel.editarReservaHora(reserva, nuevasHoras, callback);

        assertEquals(nuevasHoras, reserva.getHora().getHoras());
        verify(callback).onSuccess();
    }


}
