package com.lksnext.parkingplantilla.view.activity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityLoginBinding;
import com.lksnext.parkingplantilla.viewmodel.LoginViewModel;


public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        //Asignamos la vista/interfaz login (layout)
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Asignamos el viewModel de login
        LoginViewModel loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        //forzar teclado
        binding.emailText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.emailText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        //Acciones a realizar cuando el usuario clica el boton de login
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailText.getText().toString();
            String password = binding.passwordText.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                // Validación de campos vacíos
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar si el usuario está en Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Usuario autenticado correctamente
                            Toast.makeText(this,getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Manejar el error de autenticación
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage != null && errorMessage.contains("The password is invalid")) {
                                Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            } else if (errorMessage != null && errorMessage.contains("There is no user record corresponding to this identifier")) {
                                Toast.makeText(this, "Correo electrónico no registrado", Toast.LENGTH_SHORT).show();
                            } else if (errorMessage != null && errorMessage.contains("The email address is badly formatted")) {
                                Toast.makeText(this, "Formato de correo electrónico incorrecto", Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(this, "Error al iniciar sesión: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        //Acciones a realizar cuando el usuario clica el boton de crear cuenta (se cambia de pantalla)
        binding.btnRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });




        binding.forgotPasswordButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this,  PasswordRecoveryActivity.class);
            startActivity(intent);
                });


        //login de google
        binding.googleSignInButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, GoogleLoginActivity.class);
            startActivity(intent);
        });
    }
}