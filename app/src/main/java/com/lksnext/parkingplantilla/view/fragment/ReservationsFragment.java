package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.ParkingItem;
import com.lksnext.parkingplantilla.view.adapter.ParkingAdapter;

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

        // Añadir un item de prueba para que se muestre algo
        //TODO: Cambiar por datos reales de la base de datos
        listaDeItems.add(new ParkingItem(
                R.drawable.p2,
                "Zuaztu kalea 5 Nº 2",
                null,
                null
        ));

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ParkingAdapter adapter = new ParkingAdapter(requireContext(), listaDeItems);
        recyclerView.setAdapter(adapter);
    }
}