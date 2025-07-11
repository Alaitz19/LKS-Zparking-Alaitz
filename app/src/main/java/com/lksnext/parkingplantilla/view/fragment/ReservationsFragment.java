package com.lksnext.parkingplantilla.view.fragment;

import static com.lksnext.parkingplantilla.util.DialogUtils.setupHoraButtons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.ParkingItem;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.receiver.NotificationHelper;
import com.lksnext.parkingplantilla.view.adapter.ParkingAdapter;
import com.lksnext.parkingplantilla.viewmodel.ReservationsViewModel;
import com.lksnext.parkingplantilla.viewmodel.factory.ReservationsViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment {

    private final List<ParkingItem> listaDeItems = new ArrayList<>();
    private ReservationsViewModel reservationsViewModel;
    private ReservationsViewModelFactory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }
    public void setViewModelFactory(ReservationsViewModelFactory factory) {
        this.viewModelFactory = factory;
    }

    public void setViewModel(ReservationsViewModel viewModel) {
        this.reservationsViewModel = viewModel;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ParkingAdapter adapter = new ParkingAdapter(requireContext(), listaDeItems);
        recyclerView.setAdapter(adapter);

        if (reservationsViewModel == null) {
            DataRepository repo = new DataRepository(
                    FirebaseFirestore.getInstance(),
                    FirebaseAuth.getInstance()
            );

            if (viewModelFactory == null) {
                viewModelFactory = new ReservationsViewModelFactory(repo);
            }
            reservationsViewModel = new ViewModelProvider(this, viewModelFactory).get(ReservationsViewModel.class);
        }

        Button btnTerminadas = view.findViewById(R.id.btn_filter_terminadas);
        Button btnSiguientes = view.findViewById(R.id.btn_filter_siguientes);
        Button btnEnCurso = view.findViewById(R.id.btn_filter_encurso);



        btnTerminadas.setOnClickListener(v -> reservationsViewModel.filtrarTerminadas());
        btnSiguientes.setOnClickListener(v -> reservationsViewModel.filtrarSiguientes());
        btnEnCurso.setOnClickListener(v -> reservationsViewModel.filtrarEnCurso());

        reservationsViewModel.getReservas().observe(getViewLifecycleOwner(), reservas -> {
            listaDeItems.clear();
            for (Reserva reserva : reservas) {
                String tipoPlaza = reserva.getPlaza().getTipo();

                int imageId = switch (tipoPlaza.toLowerCase()) {
                    case "coche" -> R.drawable.coche;
                    case "moto" -> R.drawable.moto;
                    case "electrico" -> R.drawable.coche_electrico;
                    case "minusvalido" -> R.drawable.minsuvalidos;
                    default -> R.drawable.estacionamiento;
                };

                String direccion = reserva.getPlaza().getDireccion() != null
                        ? reserva.getPlaza().getDireccion()
                        : getString(R.string.reservation_no_address);

                listaDeItems.add(new ParkingItem(imageId, direccion, reserva));
            }
            adapter.notifyDataSetChanged();
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) reservationsViewModel.cargarReservas(user.getUid());

        adapter.setOnEditClickListener(item -> showEditHoraDialog(item.reserva(), reservationsViewModel));

        adapter.setOnDeleteClickListener((item, position) -> {
            Reserva reserva = item.reserva();
            if (reserva != null) {
                reservationsViewModel.borrarReserva(
                        reserva.getIdReserva(),
                        reserva.getPlaza().getCodigo(),
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                adapter.removeItem(position);
                                Toast.makeText(getContext(), getString(R.string.reservation_deleted), Toast.LENGTH_SHORT).show();
                                NotificationHelper.cancelarNotificaciones(requireContext(), reserva);
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getContext(), getString(R.string.reservation_delete_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }

    private void showEditHoraDialog(Reserva reserva, ReservationsViewModel viewModel) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filtro, null);

        dialogView.findViewById(R.id.horizontal_scroll_dias).setVisibility(View.GONE);
        dialogView.findViewById(R.id.toggle_tipo_plaza).setVisibility(View.GONE);

        GridLayout gridHoras = dialogView.findViewById(R.id.grid_horas);
        List<MaterialButton> horasSeleccionadas = new ArrayList<>();
        setupHoraButtons(requireContext(), gridHoras, horasSeleccionadas);

        MaterialButton btnGuardar = dialogView.findViewById(R.id.btn_continuar);
        btnGuardar.setText(getString(R.string.save_button));

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        btnGuardar.setOnClickListener(v -> {
            List<String> nuevasHoras = new ArrayList<>();
            for (MaterialButton btn : horasSeleccionadas) {
                nuevasHoras.add(btn.getText().toString());
            }
            if (nuevasHoras.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.select_at_least_one_hour), Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.editarReservaHora(reserva, nuevasHoras, new Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(requireContext(), getString(R.string.reservation_time_updated), Toast.LENGTH_SHORT).show();
                    viewModel.programarNotificaciones(requireContext(), reserva);
                    dialog.dismiss();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(requireContext(), getString(R.string.reservation_time_update_error), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
