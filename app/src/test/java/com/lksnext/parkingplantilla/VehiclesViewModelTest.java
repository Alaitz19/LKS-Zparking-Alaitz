package com.lksnext.parkingplantilla;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.net.Uri;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.Vehicle;
import com.lksnext.parkingplantilla.viewmodel.VehiclesViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class VehiclesViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private DataRepository repository;
    private VehiclesViewModel viewModel;

    @Before
    public void setUp() {
        repository = mock(DataRepository.class);
        viewModel = new VehiclesViewModel(repository);
    }

    @Test
    public void listenForVehicles_updatesLiveDataOnSuccess() {
        // Arrange
        List<Vehicle> mockList = Arrays.asList(mock(Vehicle.class), mock(Vehicle.class));
        doAnswer(invocation -> {
            CallbackWithResult<List<Vehicle>> callback = invocation.getArgument(0);
            callback.onSuccess(mockList);
            return null;
        }).when(repository).listenForVehicles(any());

        // ⚡ Crear el ViewModel aquí para que escuche la callback del repo
        viewModel = new VehiclesViewModel(repository);

        // Ahora observar
        Observer<List<Vehicle>> observer = mock(Observer.class);
        viewModel.getVehicles().observeForever(observer);

        // Assert: debe invocar onChanged con los resultados simulados
        verify(observer).onChanged(mockList);
    }


    @Test
    public void listenForVehicles_setsNullOnFailure() {
        // Arrange
        doAnswer(invocation -> {
            CallbackWithResult<List<Vehicle>> callback = invocation.getArgument(0);
            callback.onFailure(new Exception("Error"));
            return null;
        }).when(repository).listenForVehicles(any());

        // Debes crear el ViewModel para que listenForVehicles() se ejecute
        viewModel = new VehiclesViewModel(repository);

        // Ahora observa
        Observer<List<Vehicle>> observer = mock(Observer.class);
        viewModel.getVehicles().observeForever(observer);

        // Assert
        verify(observer).onChanged(null);
    }


    @Test
    public void addVehiculo_callsRepository() {
        String plate = "ABC123";
        String pollution = "Eco";
        String type = "Coche";
        Uri imageUri = mock(Uri.class);
        Callback callback = mock(Callback.class);

        viewModel.addVehiculo(plate, pollution, type, imageUri, callback);

        verify(repository).addVehiculo(eq(plate), eq(pollution), eq(type), eq(imageUri), eq(callback));
    }

    @Test
    public void deleteVehiculo_callsRepository() {
        String matricula = "ABC123";
        Callback callback = mock(Callback.class);

        viewModel.deleteVehiculo(matricula, callback);

        verify(repository).deleteVehiculo(eq(matricula), eq(callback));
    }

    @Test
    public void onCleared_removesListener() {
        viewModel.onCleared();
        verify(repository).removeVehiclesListener();
    }
}
