package com.lksnext.parkingplantilla.view.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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


        DataRepository repository = new DataRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance());

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkAndRequestNotificationPermission();
        createNotificationChannel();

        repository.crearPlazasIniciales(new Callback() {
            @Override
            public void onSuccess() {
                Log.d("MainActivity", "Plazas creadas o ya existentes.");
            }

            @Override
            public void onFailure() {
                Log.e("MainActivity", "Error creando plazas.");
            }
        });

        setupNavigation(binding);
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.reservas_channel_name);
            String description = getString(R.string.reservas_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(
                    "ParkingLKS_Channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupNavigation(ActivityMainBinding binding) {
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
