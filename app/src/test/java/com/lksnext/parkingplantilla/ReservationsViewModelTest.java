package com.lksnext.parkingplantilla;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Reserva;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.lksnext.parkingplantilla.viewmodel.ReservationsViewModel;

public class ReservationsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private ReservationsViewModel viewModel;
    private DataRepository mockRepo;

    @Before
    public void setUp() {
        mockRepo = mock(DataRepository.class);
        viewModel = new ReservationsViewModel(mockRepo);
    }

    @Test
    public void testCargarReservas_Success() {
        // Arrange
        String uid = "user123";
        List<Reserva> mockList = Arrays.asList(mock(Reserva.class), mock(Reserva.class));
        doAnswer(invocation -> {
            CallbackWithResult<List<Reserva>> callback = invocation.getArgument(1);
            callback.onSuccess(mockList);
            return null;
        }).when(mockRepo).getReservasUsuario(eq(uid), any());

        Observer<List<Reserva>> observer = mock(Observer.class);
        viewModel.getReservas().observeForever(observer);

        // Act
        viewModel.cargarReservas(uid);

        // Assert
        verify(observer).onChanged(mockList);
    }

    @Test
    public void testFiltrarTerminadas_EmptyWhenNoTerminadas() {
        Reserva reservaFutura = mock(Reserva.class);
        com.google.firebase.Timestamp mockTimestamp = mock(com.google.firebase.Timestamp.class);
        Hora mockHora = mock(Hora.class);

        when(reservaFutura.getFecha()).thenReturn(mockTimestamp);
        when(mockTimestamp.toDate()).thenReturn(new Date(System.currentTimeMillis() + 86400000));
        when(reservaFutura.getHora()).thenReturn(mockHora);
        when(mockHora.getHoraFin()).thenReturn("23:59");

        viewModel.todasLasReservas.add(reservaFutura);

        Observer<List<Reserva>> observer = mock(Observer.class);
        viewModel.getReservas().observeForever(observer);

        viewModel.filtrarTerminadas();

        verify(observer, atLeastOnce()).onChanged(argThat(list -> list != null && list.isEmpty()));
    }




    @Test
    public void testBorrarReserva_CallsRepoAndUpdates() {
        // Arrange
        String reservaId = "res123";
        String plazaId = "plaza1";
        Reserva reserva = mock(Reserva.class);
        when(reserva.getIdReserva()).thenReturn(reservaId);
        viewModel.todasLasReservas = new ArrayList<>();
        viewModel.todasLasReservas.add(reserva);

        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(2);
            callback.onSuccess();
            return null;
        }).when(mockRepo).borrarReservaYLiberarPlaza(eq(reservaId), eq(plazaId), any());

        Observer<List<Reserva>> observer = mock(Observer.class);
        viewModel.getReservas().observeForever(observer);

        // Act
        viewModel.borrarReserva(reservaId, plazaId, new Callback() {
            @Override public void onSuccess() {}
            @Override public void onFailure() {}
        });

        // Assert
        verify(observer, atLeastOnce()).onChanged(new ArrayList<>());  // ✅ Permite 2+ veces
    }

}
