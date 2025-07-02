package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;
import com.lksnext.parkingplantilla.domain.Callback;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DataRepository.getInstance().crearPlazasIniciales(new Callback() {
            @Override
            public void onSuccess() {
                Log.d("MainActivity", "Plazas creadas o ya existentes.");
                // Aquí puedes seguir con el flujo normal, como mostrar la UI
            }

            @Override
            public void onFailure() {
                Log.e("MainActivity", "Error creando plazas.");
                // Puedes mostrar un mensaje de error o intentar de nuevo
            }
        });

        // Configuración de la navegación inferior
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = binding.bottomNavigationView;
            NavigationUI.setupWithNavController(bottomNav, navController);

            List<Integer> bottomNavMenuItem = Arrays.asList(
                    R.id.mainFragment, R.id.reservationsFragment,
                    R.id.vehiclesFragment, R.id.profileFragment
            );

            navController.addOnDestinationChangedListener((navController1, destination, bundle) -> {
                if (bottomNavMenuItem.contains(destination.getId())) {
                    bottomNav.setVisibility(View.VISIBLE);
                } else {
                    bottomNav.setVisibility(View.GONE);
                }
            });
        }
    }

}