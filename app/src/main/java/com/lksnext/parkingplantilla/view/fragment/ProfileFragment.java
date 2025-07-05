package com.lksnext.parkingplantilla.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.databinding.FragmentProfileBinding;
import com.lksnext.parkingplantilla.domain.CallbackWithResult;
import com.lksnext.parkingplantilla.view.activity.LoginActivity;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;
import com.lksnext.parkingplantilla.viewmodel.factory.ProfileViewModelFactory;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        setupViewModel();
        setupImagePicker();
        setupListeners();
        observeUserData();
        return binding.getRoot();
    }

    private void setupViewModel() {
        DataRepository repository = new DataRepository(
                FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance()
        );
        ProfileViewModelFactory factory = new ProfileViewModelFactory(
                requireActivity().getApplication(),
                repository
        );
        profileViewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);
    }


    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            binding.buttonAvatar.setImageURI(selectedImageUri);
                            uploadImage(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void setupListeners() {
        binding.buttonAvatar.setOnClickListener(v -> openGallery());
        binding.buttonSaveProfile.setOnClickListener(v -> updateProfile());
        binding.buttonLogout.setOnClickListener(v -> logoutUser());
        binding.headerUserAccount.setOnClickListener(v -> toggleContentVisibility());
    }

    private void observeUserData() {
        profileViewModel.getUserData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            binding.editTextName.setText((String) data.get("nombre"));
            binding.editEmail.setText((String) data.get("email"));
            binding.editPhone.setText((String) data.get("telefono"));

            String imageUrl = (String) data.get("imageUrl");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.generic_avatar)
                        .into(binding.buttonAvatar);
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void uploadImage(Uri selectedImageUri) {
        profileViewModel.uploadProfileImage(selectedImageUri, new CallbackWithResult<>() {
            @Override
            public void onSuccess(String imageUrl) {
                Toast.makeText(requireContext(),
                        getString(R.string.profile_image_upload_success),
                        Toast.LENGTH_SHORT).show();
                Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.generic_avatar)
                        .into(binding.buttonAvatar);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(),
                        getString(R.string.profile_image_upload_error) + ": " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = binding.editTextName.getText().toString().trim();
        String email = binding.editEmail.getText().toString().trim();
        String phone = binding.editPhone.getText().toString().trim();

        profileViewModel.updateUserProfile(name, email, phone, new CallbackWithResult<>() {
            @Override
            public void onSuccess(Boolean result) {
                int msgRes = Boolean.TRUE.equals(result) ? R.string.profile_update_success : R.string.profile_update_error;
                Toast.makeText(requireContext(), getString(msgRes), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(),
                        getString(R.string.profile_update_error) + ": " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        profileViewModel.logout();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void toggleContentVisibility() {
        if (binding.contentUserAccount.getVisibility() == View.VISIBLE) {
            binding.contentUserAccount.setVisibility(View.GONE);
        } else {
            binding.contentUserAccount.setVisibility(View.VISIBLE);
        }
    }
}
