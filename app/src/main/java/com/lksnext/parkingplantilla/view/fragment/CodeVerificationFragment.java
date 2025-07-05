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

    private FirebaseAuth mAuth;

    private EditText inputCode;
    private Button verifyButton;
    private TextView resendCode;
    private Button backButton;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_code_verification, container, false);

        mAuth = FirebaseAuth.getInstance();


        inputCode = view.findViewById(R.id.inputCode);
        verifyButton = view.findViewById(R.id.verifyButton);
        resendCode = view.findViewById(R.id.resendCode);
        backButton = view.findViewById(R.id.backButton);

        setupListeners();

        return view;
    }

    private void setupListeners() {
        verifyButton.setOnClickListener(v -> handleVerifyCode());

        resendCode.setOnClickListener(v -> handleResendCode());

        backButton.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .popBackStack());
    }

    private void handleVerifyCode() {
        String code = inputCode.getText().toString().trim();

        if (code.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.verification_code_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        verifyCode(code);
    }

    private void handleResendCode() {
        String email = null;

        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
        }

        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.verification_email_missing), Toast.LENGTH_SHORT).show();
            return;
        }

        resendVerificationCode(email.trim());
    }

    private void resendVerificationCode(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                getString(R.string.verification_code_resent, email),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(requireContext(),
                                getString(R.string.verification_code_resend_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyCode(String code) {
        Toast.makeText(requireContext(),
                getString(R.string.verification_code_verified, code),
                Toast.LENGTH_SHORT).show();
    }
}
