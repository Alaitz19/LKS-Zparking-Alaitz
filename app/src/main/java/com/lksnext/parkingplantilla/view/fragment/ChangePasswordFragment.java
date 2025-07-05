package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.lksnext.parkingplantilla.R;

import java.util.Objects;

public class ChangePasswordFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        TextInputEditText  passwordInput = view.findViewById(R.id.passwordInput);
        TextInputEditText confirmPasswordInput = view.findViewById(R.id.confirmPasswordInput);
        Button changePasswordButton = view.findViewById(R.id.changePasswordButton);
        Button backButton = view.findViewById(R.id.backButton);

        changePasswordButton.setOnClickListener(v -> {
            String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(confirmPasswordInput.getText()).toString().trim();

            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(requireContext(),
                        getString(R.string.change_password_complete_fields),
                        Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(requireContext(),
                        getString(R.string.change_password_no_match),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        getString(R.string.change_password_success),
                        Toast.LENGTH_LONG).show();

                requireActivity().getSupportFragmentManager().popBackStack();
                requireActivity().findViewById(R.id.loginFragmentContainer).setVisibility(View.VISIBLE);
                requireActivity().findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
            requireActivity().findViewById(R.id.loginFragmentContainer).setVisibility(View.GONE);
            requireActivity().findViewById(R.id.mainContent).setVisibility(View.VISIBLE);
        });

        return view;
    }
}
