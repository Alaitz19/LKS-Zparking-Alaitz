package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lksnext.parkingplantilla.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PasswordRecoveryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery_code); // Asegúrate de que este sea el nombre correcto del layout
        EditText emailText = findViewById(R.id.emailText);
        EditText phoneText = findViewById(R.id.phoneText);
        Button sendEmailSmsButton = findViewById(R.id.sendEmailSmsButton);
        Button backButton = findViewById(R.id.backButton);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Configuración del botón para enviar correo
        sendEmailSmsButton.setOnClickListener(v -> {
            String email = emailText.getText().toString().trim();
            String phone = phoneText.getText().toString().trim();

            // Verificar si el usuario ingresó un correo o teléfono
            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Introduce un correo o número de teléfono", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si se ingresó un correo, enviamos el correo de recuperación de contraseña
            if (!TextUtils.isEmpty(email)) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordRecoveryActivity.this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show();

                                // Ir a la siguiente actividad para la verificación del código
                                Intent intent = new Intent(this, CodeVerificationActivity.class);
                                intent.putExtra("email", email);  // Pasar el email para continuar con la verificación
                                startActivity(intent);
                            } else {
                                Toast.makeText(PasswordRecoveryActivity.this, "Error al enviar el correo", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else if (!TextUtils.isEmpty(phone)) {
                
                sendVerificationCode(phone);
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ir al LoginActivity
                Intent intent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)       // Número de teléfono que se verifica
                .setTimeout(60L, TimeUnit.SECONDS)  // Tiempo de espera para el código
                .setActivity(this)                 // Actividad actual
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(com.google.firebase.auth.PhoneAuthCredential phoneAuthCredential) {
                        // El código ha sido verificado automáticamente (en algunos casos)
                        Toast.makeText(PasswordRecoveryActivity.this, "Verificación exitosa", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(com.google.firebase.FirebaseException e) {
                        Toast.makeText(PasswordRecoveryActivity.this, "Error al enviar el código", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // El código ha sido enviado, redirigimos para la verificación
                        Toast.makeText(PasswordRecoveryActivity.this, "Código de verificación enviado", Toast.LENGTH_SHORT).show();

                        // Guardamos el ID de verificación para usarlo al ingresar el código
                        Intent intent = new Intent(PasswordRecoveryActivity.this, CodeVerificationActivity.class);
                        intent.putExtra("verificationId", verificationId); // Pasar el ID de verificación
                        startActivity(intent);
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
