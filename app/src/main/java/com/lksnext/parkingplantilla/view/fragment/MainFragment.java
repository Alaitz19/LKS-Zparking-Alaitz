package com.lksnext.parkingplantilla.view.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.CallbackWithReserva;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.util.DialogUtils;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;
import com.lksnext.parkingplantilla.viewmodel.factory.MainViewModelFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainFragment extends Fragment {

    private GoogleMap mMap;
    private SearchView searchView;
    public MainViewModel viewModel;

    private final OnMapReadyCallback callback = googleMap -> {
        mMap = googleMap;

        LatLng sanSebastian = new LatLng(43.3124, -1.9839);
        float zoomLevel = 12.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sanSebastian, zoomLevel));

        LatLng parqueEmpresarialZuatzu = new LatLng(43.2980445, -2.0072874);
        googleMap.addMarker(new MarkerOptions()
                .position(parqueEmpresarialZuatzu)
                .title("Parking del Parque Empresarial de Zuatzu"));
        viewModel.cargarPlazasLibres();
        viewModel.cargarResumenPlazasAhora();
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataRepository repository = new DataRepository(
                FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance()
        );
        MainViewModelFactory factory = new MainViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);

        searchView = view.findViewById(R.id.searchView);
        setupSearchView();

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.map_container, mapFragment)
                    .commit();
        }
        mapFragment.getMapAsync(callback);

        setupFloatingButton(view);

        TextView tvWelcome = view.findViewById(R.id.tvWelcomeUser);

        viewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
            String welcomeText = getString(R.string.welcome_user) + " " + name;
            tvWelcome.setText(welcomeText);
        });
        viewModel.cargarUserName();

        viewModel.getPlazasLibres().observe(getViewLifecycleOwner(), plazas -> {
            if (plazas != null && mMap != null) {
                mostrarPlazasDisponibles(plazas);
            }
        });

        viewModel.getResumenPlazas().observe(getViewLifecycleOwner(), resumen -> {
            if (resumen != null) {
                mostrarResumenPlazas(resumen);
            }
        });

        viewModel.cargarResumenPlazasAhora();
    }

    private void mostrarResumenPlazas(Map<String, Integer> resumen) {
        LinearLayout layoutResumen = requireView().findViewById(R.id.layout_resumen_plazas);
        layoutResumen.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (Map.Entry<String, Integer> entry : resumen.entrySet()) {
            View itemView = inflater.inflate(R.layout.item_resumen_plaza, layoutResumen, false);

            TextView tvNumero = itemView.findViewById(R.id.tv_numero_libres);
            TextView tvTipo = itemView.findViewById(R.id.tv_tipo_plaza);

            tvNumero.setText(String.valueOf(entry.getValue()));
            tvTipo.setText(entry.getKey());

            layoutResumen.addView(itemView);
        }
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                geoLocate(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private static final String TAG = MainFragment.class.getSimpleName();

    private void geoLocate(String locationName) {
        if (locationName == null || locationName.isEmpty()) {
            Toast.makeText(requireContext(),
                    getString(R.string.geo_enter_address),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(locationName, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.addMarker(new MarkerOptions().position(latLng).title(locationName));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
            } else {
                Toast.makeText(requireContext(),
                        getString(R.string.geo_address_not_found),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while geocoding address: " + locationName, e);
            Toast.makeText(requireContext(),
                    getString(R.string.geo_address_search_error),
                    Toast.LENGTH_SHORT).show();
        }
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
            Toast.makeText(requireContext(),
                    getString(R.string.reservation_error_layout),
                    Toast.LENGTH_SHORT).show();
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
            button.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));
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
                    b.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white));

                }
                button.setChecked(true);
                button.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"));
            });

            layoutDias.addView(button);
            listaDias.add(button);
            if (i == 0) button.performClick();
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

    public void handleContinuarClick(String date, String spotType, List<MaterialButton> selectedButtons, android.app.AlertDialog dialog) {
        List<String> selectedHours = new ArrayList<>();
        for (MaterialButton b : selectedButtons) {
            selectedHours.add(b.getText().toString());
        }

        if (date == null || spotType == null || selectedHours.isEmpty()) {
            Toast.makeText(requireContext(),
                    getString(R.string.reservation_select_all),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedHours.size() < 2) {
            Toast.makeText(requireContext(),
                    getString(R.string.reservation_min_two_hours),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        selectedHours.sort(String::compareTo);

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
            Toast.makeText(requireContext(),
                    getString(R.string.reservation_hours_consecutive),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String startHour = selectedHours.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            long startMillis = Objects.requireNonNull(sdf.parse(date + " " + startHour)).getTime();
            long now = System.currentTimeMillis();

            if (startMillis < now) {
                Toast.makeText(requireContext(),
                        getString(R.string.reservation_time_in_past),
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(),
                    getString(R.string.reservation_time_validation_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.reservarSiLibre(requireContext(), date, selectedHours, spotType, new CallbackWithReserva() {
            @Override
            public void onSuccess(Reserva reserva) {
                Log.d("MainFragment", "Reservation successful");
                dialog.dismiss();
                NavController navController = Navigation.findNavController(requireView());
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.mainFragment, true)
                        .build();
                navController.navigate(R.id.action_mainFragment_to_reservationsFragment, null, navOptions);
            }

            @Override
            public void onFailure() {
                Toast.makeText(requireContext(),
                        getString(R.string.reservation_spot_not_available),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BitmapDescriptor createPlazaMarker(int numero) {
        ViewGroup root = (ViewGroup) requireView();
        View markerView = getLayoutInflater().inflate(R.layout.marker_plaza, root, false);
        TextView tvNumero = markerView.findViewById(R.id.tv_plaza_numero);
        tvNumero.setText(String.valueOf(numero));
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void mostrarPlazasDisponibles(List<Plaza> plazas) {
        for (int i = 0; i < plazas.size(); i++) {
            Plaza plaza = plazas.get(i);
            LatLng ubicacion = obtenerUbicacionPlaza(plaza.getCodigo());
            if (ubicacion == null) continue;

            mMap.addMarker(new MarkerOptions()
                    .position(ubicacion)
                    .icon(createPlazaMarker(i + 1))
                    .title(getString(R.string.marker_plaza_title, plaza.getCodigo())));
        }
    }

    private LatLng obtenerUbicacionPlaza(String codigo) {
        return switch (codigo) {
            case "COC001" -> new LatLng(43.2981, -2.0072);
            case "COC002" -> new LatLng(43.2982, -2.0073);
            default -> null;
        };
    }
}
