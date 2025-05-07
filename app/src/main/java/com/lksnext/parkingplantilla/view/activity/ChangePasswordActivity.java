package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.lksnext.parkingplantilla.R;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText passwordInput, confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Referencias a los inputs
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        Button changePasswordButton = findViewById(R.id.changePasswordButton);
        Button backButton = findViewById(R.id.backButton);

        changePasswordButton.setOnClickListener(v -> {
            String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString().trim();

            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Por favor, completa ambos campos", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show();

                // Redirigir al login
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });

        backButton.setOnClickListener(v -> {
            finish(); // Volver a la pantalla anterior
        });
    }
}
