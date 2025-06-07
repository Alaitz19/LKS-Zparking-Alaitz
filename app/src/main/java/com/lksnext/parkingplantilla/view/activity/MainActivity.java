package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_USER_NAME = "USER_NAME";

    private String userName;

    BottomNavigationView bottomNavigationView;
    ActivityMainBinding binding;
    NavController navController;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz main (layout)
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Con el NavigationHost podremos movernos por distintas pestañas dentro de la misma pantalla
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.flFragment);
        navController = navHostFragment.getNavController();

        //Asignamos los botones de navegacion que se encuentran en la vista (layout)
        bottomNavigationView = binding.bottomNavigationView;
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //Dependendiendo que boton clique el usuario de la navegacion se hacen distintas cosas
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.newres) {
                Intent intent = new Intent(this, NewReservationActivity.class);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.reservations) {
                Intent intent = new Intent(this, ReservationsActivity.class);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.vehicles) {
                Intent intent = new Intent(this, VehiclesActivity.class);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.person) {
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }

            return false;
        });


        userName = getIntent().getStringExtra(EXTRA_USER_NAME);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    public String getUserName() {
        return userName != null ? userName : "Guest";
    }
}