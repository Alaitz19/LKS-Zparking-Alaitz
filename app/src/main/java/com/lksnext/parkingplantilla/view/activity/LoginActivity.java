package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.FirebaseApp;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityLoginBinding;
import com.lksnext.parkingplantilla.view.fragment.RegisterFragment;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegisterButton.setOnClickListener(v -> {
            // Oculta el layout principal del login
            binding.getRoot().setVisibility(View.GONE);
            // Muestra el contenedor de fragmentos
            findViewById(R.id.loginFragmentContainer).setVisibility(View.VISIBLE);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.loginFragmentContainer, new RegisterFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }



}