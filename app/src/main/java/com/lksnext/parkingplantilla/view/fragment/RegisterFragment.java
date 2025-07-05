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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.databinding.FragmentRegisterBinding;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.viewmodel.RegisterViewModel;
import com.lksnext.parkingplantilla.viewmodel.factory.RegisterViewModelFactory;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FragmentRegisterBinding binding = FragmentRegisterBinding.inflate(inflater, container, false);

        // ✅ Inyección con Factory
        DataRepository repo = new DataRepository(
                FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance()
        );
        RegisterViewModelFactory factory = new RegisterViewModelFactory(repo);
        RegisterViewModel registerViewModel = new ViewModelProvider(this, factory).get(RegisterViewModel.class);

        binding.registerButton.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.emailEditText.getText()).toString().trim();
            String password = Objects.requireNonNull(binding.passwordEditText.getText()).toString().trim();
            String username = Objects.requireNonNull(binding.usernameEditText.getText()).toString().trim();
            String phone = binding.phoneEditText.getText().toString().trim();
            String confirmPassword = Objects.requireNonNull(binding.confirmPasswordEditText.getText()).toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(R.string.register_invalid_email);
                return;
            }

            if (!password.equals(confirmPassword)) {
                showToast(R.string.register_passwords_no_match);
                return;
            }

            registerViewModel.register(email, password, username, phone, new Callback() {
                @Override
                public void onSuccess() {
                    requireActivity().runOnUiThread(() -> {
                        showToast(R.string.register_success);
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.loginFragmentContainer, new LoginFragment())
                                .addToBackStack(null)
                                .commit();
                    });
                }

                @Override
                public void onFailure() {
                    requireActivity().runOnUiThread(() -> showToast(R.string.register_error));
                }
            });
        });

        binding.backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return binding.getRoot();
    }

    private void showToast(int resId) {
        Toast.makeText(getContext(), getString(resId), Toast.LENGTH_SHORT).show();
    }
}
