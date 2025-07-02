package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.ParkingItem;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.view.adapter.ParkingAdapter;
import com.lksnext.parkingplantilla.viewmodel.ReservationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment {

    private final List<ParkingItem> listaDeItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ParkingAdapter adapter = new ParkingAdapter(requireContext(), listaDeItems);
        recyclerView.setAdapter(adapter);

        ReservationsViewModel reservationsViewModel = new ViewModelProvider(this).get(ReservationsViewModel.class);

        reservationsViewModel.getReservas().observe(getViewLifecycleOwner(), reservas -> {
            listaDeItems.clear();
            for (Reserva reserva : reservas) {
                // Puedes personalizar la imagen y la dirección según la plaza o tipo de plaza
                int imageId = R.drawable.p2; // Usa la imagen que prefieras
                String direccion = "Dirección no disponible"; // O busca la dirección real si la tienes
                listaDeItems.add(new ParkingItem(imageId, direccion, reserva));
            }
            adapter.notifyDataSetChanged();
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) reservationsViewModel.cargarReservas(user.getUid());
        adapter.setOnDeleteClickListener((item, position) -> {
            Reserva reserva = item.getReserva();
            if (reserva != null) {
                reservationsViewModel.borrarReserva(
                        reserva.getIdReserva(),               // 🔑 Firestore document ID
                        reserva.getPlaza().getCodigo(),       // 🔑 ID de la plaza
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                adapter.removeItem(position);
                                Toast.makeText(getContext(), "Reserva eliminada", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getContext(), "Error al borrar reserva", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(ReservationsFragment.this)
                        .navigate(R.id.action_reservationsFragment_to_mainFragment);
            }
        });
    }
}