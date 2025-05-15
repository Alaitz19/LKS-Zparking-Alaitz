package com.lksnext.parkingplantilla.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityLoginBinding;
import com.lksnext.parkingplantilla.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Iniciar el ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Forzar teclado al entrar en el campo de email
        binding.emailText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.emailText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        // Observador del estado de login
        loginViewModel.isLogged().observe(this, isLogged -> {
            if (isLogged == null) return;
            if (isLogged) {
                mostrarToast(R.string.login_success);
                irAMainActivity();
            } else {
                mostrarToast(R.string.login_error);
            }
        });

        // Botón de login
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailText.getText().toString();
            String password = binding.passwordText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            loginViewModel.loginUser(email, password);
        });

        // Botón de registro
        binding.btnRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Botón de recuperar contraseña
        binding.forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
            startActivity(intent);
        });

        // Botón de login con Google
        binding.googleSignInButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, GoogleLoginActivity.class);
            startActivity(intent);
        });
    }

    private void irAMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarToast(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}
