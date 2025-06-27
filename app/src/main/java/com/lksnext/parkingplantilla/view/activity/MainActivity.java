package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        NavHostFragment navHostFragment = binding.flFragment.getFragment();
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = binding.bottomNavigationView;

        NavigationUI.setupWithNavController(bottomNav, navController);

        List<Integer> bottomNavMenuItem = List.of(R.id.mainFragment, R.id.reservationsFragment,
                R.id.vehiclesFragment, R.id.profileFragment);

        navController.addOnDestinationChangedListener((navController1, destination, bundle) -> {
            if (bottomNavMenuItem.contains(destination.getId())) {
                bottomNav.setVisibility(View.VISIBLE);
            } else {
                bottomNav.setVisibility(View.GONE);
            }
        });

        setContentView(binding.getRoot());
    }

    public String getUserName() {
        // Aquí deberías implementar la lógica para obtener el nombre del usuario actual
        // Por ejemplo, si estás usando Firebase Authentication, podrías hacer algo como:
        // return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        // Para este ejemplo, simplemente retornaremos un nombre de usuario ficticio.
        return "Usuario Ejemplo";
    }
}