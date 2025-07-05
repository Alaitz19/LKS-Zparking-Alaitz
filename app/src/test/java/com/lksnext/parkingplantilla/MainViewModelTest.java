package com.lksnext.parkingplantilla;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private DataRepository repository;
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        repository = mock(DataRepository.class);
        viewModel = new MainViewModel(repository);
    }

    @Test
    public void testCargarUserName_WhenNameExists() {
        when(repository.getUserName()).thenReturn("Alaitz");

        viewModel.cargarUserName();

        assertEquals("Alaitz", viewModel.getUserName().getValue());
    }

    @Test
    public void testCargarUserName_WhenNameIsNull() {
        when(repository.getUserName()).thenReturn(null);

        viewModel.cargarUserName();

        assertEquals("Usuario", viewModel.getUserName().getValue());
    }

    @Test
    public void testCargarPlazasLibres_Success() {
        doAnswer(invocation -> {
            CallbackWithResult<List<Plaza>> callback = invocation.getArgument(0);
            callback.onSuccess(Collections.emptyList());
            return null;
        }).when(repository).getPlazasLibres(any());

        viewModel.cargarPlazasLibres();

        assertNotNull(viewModel.getPlazasLibres().getValue());
    }

    @Test
    public void testCargarPlazasLibres_Failure() {
        doAnswer(invocation -> {
            CallbackWithResult<List<Plaza>> callback = invocation.getArgument(0);
            callback.onFailure(new Exception());
            return null;
        }).when(repository).getPlazasLibres(any());

        viewModel.cargarPlazasLibres();

        assertNull(viewModel.getPlazasLibres().getValue());
    }

    @Test
    public void testCargarResumenPlazasAhora_Success() {
        Map<String, Integer> fakeResumen = new HashMap<>();
        fakeResumen.put("Coche", 5);

        doAnswer(invocation -> {
            CallbackWithResult<Map<String, Integer>> callback = invocation.getArgument(0);
            callback.onSuccess(fakeResumen);
            return null;
        }).when(repository).getResumenPlazasLibresAhora(any());

        viewModel.cargarResumenPlazasAhora();

        assertEquals(fakeResumen, viewModel.getResumenPlazas().getValue());
    }

    @Test
    public void testCargarResumenPlazasAhora_Failure() {
        doAnswer(invocation -> {
            CallbackWithResult<Map<String, Integer>> callback = invocation.getArgument(0);
            callback.onFailure(new Exception());
            return null;
        }).when(repository).getResumenPlazasLibresAhora(any());

        viewModel.cargarResumenPlazasAhora();

        assertNull(viewModel.getResumenPlazas().getValue());
    }
}
