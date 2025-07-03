package com.lksnext.parkingplantilla.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.view.activity.LoginActivity;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private ImageButton avatarButton;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private EditText editName, editEmail, editPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication())
        ).get(ProfileViewModel.class);

        // Referencias de UI
        avatarButton = view.findViewById(R.id.buttonAvatar);
        editName = view.findViewById(R.id.editTextName);
        editEmail = view.findViewById(R.id.editEmail);
        editPhone = view.findViewById(R.id.editPhone);
        Button saveButton = view.findViewById(R.id.buttonSaveProfile);
        Button logoutButton = view.findViewById(R.id.buttonLogout);

        // Cargar datos actuales
        profileViewModel.getUserData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                editName.setText((String) data.get("nombre"));
                editEmail.setText((String) data.get("email"));
                editPhone.setText((String) data.get("telefono"));

                String imageUrl = (String) data.get("imageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.generic_avatar)
                            .into(avatarButton);
                }
            }
        });

        // Setup launcher galería
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Previsualiza localmente
                            avatarButton.setImageURI(selectedImageUri);

                            // Súbela a Storage y actualiza Firestore
                            profileViewModel.uploadProfileImage(selectedImageUri, new CallbackWithResult<String>() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    Toast.makeText(getActivity(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                                    // Vuelve a cargar desde URL (opcional)
                                    Glide.with(requireContext())
                                            .load(imageUrl)
                                            .placeholder(R.drawable.generic_avatar)
                                            .into(avatarButton);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
        );

        avatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        saveButton.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();

            profileViewModel.updateUserProfile(name, email, phone, new CallbackWithResult<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (result) {
                        Toast.makeText(getActivity(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        logoutButton.setOnClickListener(v -> {
            profileViewModel.logout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Sección expandible
        TextView header = view.findViewById(R.id.headerUserAccount);
        LinearLayout content = view.findViewById(R.id.contentUserAccount);
        header.setOnClickListener(v -> {
            if (content.getVisibility() == View.VISIBLE) {
                content.setVisibility(View.GONE);
            } else {
                content.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }
}
