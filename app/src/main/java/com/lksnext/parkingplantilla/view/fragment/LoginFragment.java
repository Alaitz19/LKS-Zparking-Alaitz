package com.lksnext.parkingplantilla.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        setupListeners();
        observeLoginState();

        return binding.getRoot();
    }

    private void setupListeners() {
        binding.loginButton.setOnClickListener(v -> {
            String email = getTrimmedText(binding.email.getEditText());
            String password = getTrimmedText(binding.password.getEditText());

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(requireContext(), getString(R.string.login_empty_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            loginViewModel.loginUser(email, password);
        });

        binding.btnRegisterButton.setOnClickListener(v -> navigateTo(new RegisterFragment()));
        binding.forgotPasswordButton.setOnClickListener(v -> navigateTo(new PasswordRecoveryFragment()));

        binding.googleSignInButton.setOnClickListener(v ->
                ((LoginActivity) requireActivity()).launchGoogleSignIn()
        );
    }

    private void observeLoginState() {
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
    }

    private void navigateTo(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.loginFragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private String getTrimmedText(android.widget.EditText editText) {
        return editText != null ? editText.getText().toString().trim() : "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Evita fugas de memoria
    }
}
