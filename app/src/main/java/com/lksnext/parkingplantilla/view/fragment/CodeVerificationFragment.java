package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.R;

public class CodeVerificationFragment extends Fragment {

    private EditText inputCode;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_verification, container, false);

        mAuth = FirebaseAuth.getInstance();
        inputCode = view.findViewById(R.id.inputCode);
        Button verifyButton = view.findViewById(R.id.verifyButton);
        TextView resendCode = view.findViewById(R.id.resendCode);
        Button backButton = view.findViewById(R.id.backButton);

        verifyButton.setOnClickListener(v -> {
            String code = inputCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, introduce el código", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyCode(code);
        });

        resendCode.setOnClickListener(v -> {
            String email = getArguments() != null ? getArguments().getString("email") : null;
            if (email == null || email.isEmpty()) {
                Toast.makeText(requireContext(), "No se encontró un correo electrónico válido", Toast.LENGTH_SHORT).show();
                return;
            }
            resendVerificationCode(email);
        });

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void resendVerificationCode(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Código reenviado al correo: " + email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(), "Error al reenviar el código", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyCode(String code) {
        // Implementar la lógica de verificación del código
        Toast.makeText(requireContext(), "Código verificado: " + code, Toast.LENGTH_SHORT).show();
    }
}