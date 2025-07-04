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
            android.widget.Toast.makeText(requireContext(), "Error in the layout", android.widget.Toast.LENGTH_SHORT).show();
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

    private void handleContinuarClick(String date, String spotType, List<MaterialButton> selectedButtons, android.app.AlertDialog dialog) {
        List<String> selectedHours = new ArrayList<>();
        for (MaterialButton b : selectedButtons) {
            selectedHours.add(b.getText().toString());
        }

        if (date == null || spotType == null || selectedHours.isEmpty()) {
            android.widget.Toast.makeText(requireContext(),
                    "Please select a date, spot type, and at least one hour.",
                    android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedHours.size() < 2) {
            android.widget.Toast.makeText(requireContext(),
                    "You must select at least two consecutive hours.",
                    android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Sort the hours
        selectedHours.sort(String::compareTo);

        // Check that the selected hours are consecutive
        boolean areConsecutive = true;
        try {
            for (int i = 0; i < selectedHours.size() - 1; i++) {
                String[] currentParts = selectedHours.get(i).split(":");
                String[] nextParts = selectedHours.get(i + 1).split(":");

                int currentMinutes = Integer.parseInt(currentParts[0]) * 60 + Integer.parseInt(currentParts[1]);
                int nextMinutes = Integer.parseInt(nextParts[0]) * 60 + Integer.parseInt(nextParts[1]);

                if (nextMinutes - currentMinutes != 30) {
                    areConsecutive = false;
                    break;
                }
            }
        } catch (Exception e) {
            areConsecutive = false;
        }

        if (!areConsecutive) {
            android.widget.Toast.makeText(requireContext(),
                    "Selected hours must be consecutive with no gaps.",
                    android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that the first hour is not in the past
        try {
            String startHour = selectedHours.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            long startMillis = sdf.parse(date + " " + startHour).getTime();

            long now = System.currentTimeMillis();
            if (startMillis < now) {
                android.widget.Toast.makeText(requireContext(),
                        "You can't reserve a time slot in the past.",
                        android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (Exception e) {
            android.widget.Toast.makeText(requireContext(),
                    "Error validating the selected time.",
                    android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // If all checks pass, continue with reservation
        viewModel.reservarSiLibre(requireContext(), date, selectedHours, spotType, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("MainFragment", "Reservation successful");
                dialog.dismiss();

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.mainFragment, true)
                        .build();
                navController.navigate(R.id.action_mainFragment_to_reservationsFragment, null, navOptions);
            }

            @Override
            public void onFailure() {
                android.widget.Toast.makeText(requireContext(),
                        "The parking spot is not available at the selected time.",
                        android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }




}
