package com.lksnext.parkingplantilla.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentLoginBinding;
import com.lksnext.parkingplantilla.view.activity.LoginActivity;
import com.lksnext.parkingplantilla.view.activity.MainActivity;
import com.lksnext.parkingplantilla.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        binding.loginButton.setOnClickListener(v -> {
            String email = binding.email.getEditText().getText().toString();
            String password = binding.password.getEditText().getText().toString();
            loginViewModel.loginUser(email, password);
        });

        binding.btnRegisterButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.loginFragmentContainer, new RegisterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.forgotPasswordButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.loginFragmentContainer, new PasswordRecoveryFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.googleSignInButton.setOnClickListener(v -> {
            ((LoginActivity) requireActivity()).launchGoogleSignIn();
        });

        loginViewModel.isLogged().observe(getViewLifecycleOwner(), logged -> {
            if (Boolean.TRUE.equals(logged)) {
                Toast.makeText(requireContext(),
                        getString(R.string.login_success),
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else if (Boolean.FALSE.equals(logged)) {
                Toast.makeText(requireContext(),
                        getString(R.string.login_failed),
                        Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }
}
