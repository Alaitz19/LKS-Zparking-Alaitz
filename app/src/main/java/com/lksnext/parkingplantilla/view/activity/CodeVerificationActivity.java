package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.R;

public class CodeVerificationActivity extends AppCompatActivity {

    private EditText inputCode;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);
        mAuth = FirebaseAuth.getInstance();
        inputCode = findViewById(R.id.inputCode);
        Button verifyButton = findViewById(R.id.verifyButton);
        TextView resendCode = findViewById(R.id.resendCode);
        Button backButton = findViewById(R.id.backButton);

        verifyButton.setOnClickListener(v -> {
            String code = inputCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce el código", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyCode(code);
        });

        resendCode.setOnClickListener(v -> {
            String email = getIntent().getStringExtra("email");
            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "No se encontró un correo electrónico válido", Toast.LENGTH_SHORT).show();
                return;
            }

            resendVerificationCode(email);
        });
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CodeVerificationActivity.this, PasswordRecoveryActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void resendVerificationCode(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Código reenviado al correo: " + email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al reenviar el código", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyCode(String code) {
        // Implementar la lógica de verificación del código
        Toast.makeText(this, "Código verificado: " + code, Toast.LENGTH_SHORT).show();
    }


}
