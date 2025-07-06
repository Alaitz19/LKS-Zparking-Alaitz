package com.lksnext.parkingplantilla;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;




import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.fragment.app.testing.FragmentScenario.FragmentAction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.button.MaterialButton;
import com.lksnext.parkingplantilla.domain.CallbackWithReserva;
import com.lksnext.parkingplantilla.view.fragment.MainFragment;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.doAnswer;

import android.app.AlertDialog;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MainFragmentTest {

    @Mock
    MainViewModel mockViewModel;

    @Mock
    NavController mockNavController;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testReservarSiLibre_successNavigatesToReservations() {
        // Lanza el Fragment con FragmentScenario
        FragmentScenario<MainFragment> scenario = FragmentScenario.launchInContainer(
                MainFragment.class, null, R.style.Theme_ParkingLKS);

        scenario.onFragment(new FragmentAction<MainFragment>() {
            @Override
            public void perform(@NonNull MainFragment fragment) {
                // Inyecta el NavController falso
                Navigation.setViewNavController(fragment.requireView(), mockNavController);

                // Inyecta el ViewModel falso
                fragment.viewModel = mockViewModel;


                // Simula que al invocar reservarSiLibre se llama onSuccess del callback
                doAnswer(invocation -> {
                    CallbackWithReserva callback = invocation.getArgument(4);
                    callback.onSuccess(null); // Simula reserva exitosa
                    return null;
                }).when(mockViewModel).reservarSiLibre(
                        eq(fragment.requireContext()),
                        any(),
                        any(),
                        any(),
                        any()
                );

                MaterialButton button1 = org.mockito.Mockito.mock(MaterialButton.class);
                MaterialButton button2 = org.mockito.Mockito.mock(MaterialButton.class);


                org.mockito.Mockito.when(button1.getText()).thenReturn("10:00");
                org.mockito.Mockito.when(button2.getText()).thenReturn("10:30");

                List<MaterialButton> selectedButtons = List.of(button1, button2);

                fragment.handleContinuarClick(
                        "2025-09-06",
                        "Coche",
                        selectedButtons,
                        new AlertDialog.Builder(fragment.requireContext()).create()
                );

                // Verifica navegación
                verify(mockNavController).navigate(
                        eq(R.id.action_mainFragment_to_reservationsFragment),
                        eq(null),
                        any()
                );
            }
        });
    }
}
