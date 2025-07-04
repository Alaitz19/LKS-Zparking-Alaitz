package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.util.DialogUtils;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;
import com.lksnext.parkingplantilla.domain.Callback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainFragment extends Fragment {

    private MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        setupFloatingButton(view);

        return view;
    }

    private void setupFloatingButton(View view) {
        View fab = view.findViewById(R.id.btn_new_reservation);
        fab.setOnClickListener(v -> showReservationDialog());
    }

    private void showReservationDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_filtro, null);
        builder.setView(dialogView);

        LinearLayout layoutDias = dialogView.findViewById(R.id.layout_dias);
        GridLayout gridHoras = dialogView.findViewById(R.id.grid_horas);
        MaterialButton btnContinuar = dialogView.findViewById(R.id.btn_continuar);

        if (layoutDias == null || gridHoras == null || btnContinuar == null) {
            android.widget.Toast.makeText(requireContext(), "Error en el layout del diálogo", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        final String[] fechaSeleccionada = {null};
        final List<MaterialButton> listaDias = new ArrayList<>();
        setupDiaButtons(layoutDias, fechaSeleccionada, listaDias);

        final List<MaterialButton> horasSeleccionadas = new ArrayList<>();

        DialogUtils.setupHoraButtons(requireContext(), gridHoras, horasSeleccionadas);


        final String[] tipoPlaza = {null};
        setupTipoPlazaSelection(dialogView, tipoPlaza);

        final android.app.AlertDialog dialog = builder.create();

        btnContinuar.setOnClickListener(view -> handleContinuarClick(fechaSeleccionada[0], tipoPlaza[0], horasSeleccionadas, dialog));

        dialog.show();
    }

    private void setupDiaButtons(LinearLayout layoutDias, String[] fechaSeleccionada, List<MaterialButton> listaDias) {
        layoutDias.removeAllViews();
        SimpleDateFormat formatter = new SimpleDateFormat("d\nE", Locale.getDefault());
        Calendar today = Calendar.getInstance();

        for (int i = 0; i <= 6; i++) {
            Calendar date = (Calendar) today.clone();
            date.add(Calendar.DAY_OF_MONTH, i);
            String buttonText = formatter.format(date.getTime());
            String fechaISO = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.getTime());

            MaterialButton button = new MaterialButton(requireContext(), null);
            button.setBackgroundColor(getResources().getColor(android.R.color.white)); // Desmarcado: blanco
            button.setText(buttonText);
            button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            button.setTextSize(14);
            button.setAllCaps(false);
            button.setCheckable(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd((int) (getResources().getDisplayMetrics().density * 8));
            button.setLayoutParams(params);

            button.setOnClickListener(btn -> {
                fechaSeleccionada[0] = fechaISO;

                for (MaterialButton b : listaDias) {
                    b.setChecked(false);
                    b.setBackgroundColor(getResources().getColor(android.R.color.white)); // Todos a blanco
                }

                button.setChecked(true);
                button.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC")); // Seleccionado: gris
            });

            layoutDias.addView(button);
            listaDias.add(button);

            if (i == 0) button.performClick(); // Selección por defecto
        }
    }

    private void setupTipoPlazaSelection(View dialogView, String[] tipoPlaza) {
        MaterialButton btnCoche = dialogView.findViewById(R.id.btn_coche);
        MaterialButton btnAdaptado = dialogView.findViewById(R.id.btn_adaptado);
        MaterialButton btnMoto = dialogView.findViewById(R.id.btn_moto);
        MaterialButton btnElectrico = dialogView.findViewById(R.id.btn_electrico);

        if (btnCoche != null) btnCoche.setOnClickListener(b -> tipoPlaza[0] = "Coche");
        if (btnAdaptado != null) btnAdaptado.setOnClickListener(b -> tipoPlaza[0] = "Discapacitado");
        if (btnMoto != null) btnMoto.setOnClickListener(b -> tipoPlaza[0] = "Moto");
        if (btnElectrico != null) btnElectrico.setOnClickListener(b -> tipoPlaza[0] = "Electrico");
    }

    private void handleContinuarClick(String fecha, String tipoPlaza, List<MaterialButton> horasSeleccionadas, android.app.AlertDialog dialog) {
        List<String> horas = new ArrayList<>();
        for (MaterialButton b : horasSeleccionadas) {
            horas.add(b.getText().toString());
        }

        if (fecha == null || tipoPlaza == null || horas.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), "Selecciona fecha, tipo de plaza y al menos una hora", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.reservarSiLibre( requireContext(),fecha, horas, tipoPlaza, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("MainFragment", "Reserva exitosa");
                dialog.dismiss();

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.mainFragment, true)
                        .build();
                navController.navigate(R.id.action_mainFragment_to_reservationsFragment, null, navOptions);

            }

            @Override
            public void onFailure() {
                android.widget.Toast.makeText(requireContext(), "La plaza no está disponible en ese horario", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
