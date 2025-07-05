package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lksnext.parkingplantilla.R;

import java.util.concurrent.TimeUnit;

public class PasswordRecoveryFragment extends Fragment {





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EditText emailText;
        EditText phoneText;
        View view = inflater.inflate(R.layout.fragment_password_recovery_code, container, false);

        emailText = view.findViewById(R.id.emailText);
        phoneText = view.findViewById(R.id.phoneText);
        Button sendEmailSmsButton = view.findViewById(R.id.sendEmailSmsButton);
        Button backButton = view.findViewById(R.id.backButton);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        sendEmailSmsButton.setOnClickListener(v -> {
            String email = emailText.getText().toString().trim();
            String phone = phoneText.getText().toString().trim();

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(phone)) {
                Toast.makeText(requireContext(),
                        getString(R.string.recovery_enter_email_or_phone),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!TextUtils.isEmpty(email)) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(),
                                        getString(R.string.recovery_email_sent),
                                        Toast.LENGTH_SHORT).show();
                                Bundle args = new Bundle();
                                args.putString("email", email);
                                CodeVerificationFragment fragment = new CodeVerificationFragment();
                                fragment.setArguments(args);
                                requireActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.bottomNavigationView, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                Toast.makeText(requireContext(),
                                        getString(R.string.recovery_email_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else if (!TextUtils.isEmpty(phone)) {
                sendVerificationCode(phone);
            }
        });

        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull com.google.firebase.auth.PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(requireContext(),
                                getString(R.string.recovery_verification_success),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull com.google.firebase.FirebaseException e) {
                        Toast.makeText(requireContext(),
                                getString(R.string.recovery_code_error),
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        Toast.makeText(requireContext(),
                                getString(R.string.recovery_code_sent),
                                Toast.LENGTH_SHORT).show();
                        Bundle args = new Bundle();
                        args.putString("verificationId", verificationId);
                        CodeVerificationFragment fragment = new CodeVerificationFragment();
                        fragment.setArguments(args);
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.bottomNavigationView, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                })
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
