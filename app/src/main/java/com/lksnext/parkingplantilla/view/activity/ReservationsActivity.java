package com.lksnext.parkingplantilla.view.activity;

import static androidx.databinding.DataBindingUtil.setContentView;

import static java.security.AccessController.getContext;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.ParkingItem;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.view.fragment.ParkingAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReservationsActivity extends AppCompatActivity {

    List<ParkingItem> listaDeItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        // Añadir un item de prueba para que se muestre algo
        //TODO: Cambiar por datos reales de la base de datos
        listaDeItems.add(new ParkingItem(
                R.drawable.p2,
                "Zuaztu kalea 5 Nº 2",
                null,
                null
        ));

        RecyclerView recyclerView = findViewById(R.id.recycler_view_reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ParkingAdapter adapter = new ParkingAdapter(this, listaDeItems);
        recyclerView.setAdapter(adapter);



        // Aquí puedes inicializar tus componentes de la interfaz de usuario
        // y configurar la lógica para mostrar las reservas.
    }
}
