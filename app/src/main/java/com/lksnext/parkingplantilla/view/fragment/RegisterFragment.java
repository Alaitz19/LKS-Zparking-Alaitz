package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.databinding.FragmentRegisterBinding;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.viewmodel.RegisterViewModel;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);


        binding.registerButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();
            String username = binding.usernameEditText.getText().toString().trim();
            String phone = binding.phoneEditText.getText().toString().trim();
            String confirmPassword = binding.confirmPasswordEditText.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Email no válido", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            registerViewModel.register(email, password, username, phone, new Callback() {
                @Override
                public void onSuccess() {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onFailure() {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Error en el registro", Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });

        binding.backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
            requireActivity().findViewById(com.lksnext.parkingplantilla.R.id.loginFragmentContainer).setVisibility(View.GONE);
            requireActivity().findViewById(android.R.id.content).setVisibility(View.VISIBLE);
        });

        return binding.getRoot();
    }
}