package com.lksnext.parkingplantilla.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.view.adapter.VehicleAdapter;
import com.lksnext.parkingplantilla.viewmodel.VehiclesViewModel;
import com.lksnext.parkingplantilla.viewmodel.factory.VehiclesViewModelFactory;

import java.util.ArrayList;

public class VehiclesFragment extends Fragment {

    private VehiclesViewModel viewModel;
    private Uri selectedImageUri;
    private ImageView dialogImageView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vehicles, container, false);

        setupViewModel();
        setupRecyclerView(view);
        setupImagePickerLauncher();
        setupAddVehicleButton(view);

        return view;
    }

    private void setupViewModel() {
        DataRepository repository = new DataRepository(
                FirebaseFirestore.getInstance(),
                FirebaseAuth.getInstance()
        );
        VehiclesViewModelFactory factory = new VehiclesViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(VehiclesViewModel.class);
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.vehicles_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        VehicleAdapter adapter = new VehicleAdapter(new ArrayList<>(), vehicle ->
                viewModel.deleteVehiculo(vehicle.getMatricula(), new Callback() {
                    @Override
                    public void onSuccess() {
                        showToast(R.string.vehicle_deleted_success);
                    }

                    @Override
                    public void onFailure() {
                        showToast(R.string.vehicle_deleted_failure);
                    }
                })
        );
        recyclerView.setAdapter(adapter);

        viewModel.getVehicles().observe(getViewLifecycleOwner(), adapter::setVehicles);
    }

    private void setupAddVehicleButton(View view) {
        View addVehicleButton = view.findViewById(R.id.add_vehicle_button);
        addVehicleButton.setOnClickListener(v -> showAddVehicleDialog());
    }

    private void setupImagePickerLauncher() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (dialogImageView != null) {
                            dialogImageView.setImageURI(selectedImageUri);
                        }
                    }
                }
        );
    }

    private void showAddVehicleDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_vehicle, null);

        EditText editTextPlate = dialogView.findViewById(R.id.editTextPlate);
        Spinner spinnerPollution = dialogView.findViewById(R.id.spinnerPollution);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);
        dialogImageView = dialogView.findViewById(R.id.imageViewVehicle);

        dialogImageView.setOnClickListener(v -> openGallery());

        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.dialog_add_vehicle_title)
                .setView(dialogView)
                .setPositiveButton(R.string.dialog_add_vehicle_positive, (dialog, which) -> {
                    String plate = editTextPlate.getText().toString().trim();
                    String pollutionType = spinnerPollution.getSelectedItem().toString();
                    String selectedType = spinnerType.getSelectedItem().toString();

                    if (plate.isEmpty()) {
                        showToast(R.string.error_plate_required);
                        return;
                    }

                    if (selectedImageUri != null) {
                        addVehicle(plate, pollutionType, selectedType);
                    } else {
                        showToast(R.string.error_image_required);
                    }

                    selectedImageUri = null;
                })
                .setNegativeButton(R.string.dialog_add_vehicle_negative, (dialog, which) -> {
                    dialog.dismiss();
                    selectedImageUri = null;
                })
                .create()
                .show();
    }

    private void addVehicle(String plate, String pollutionType, String type) {
        viewModel.addVehiculo(plate, pollutionType, type, selectedImageUri, new Callback() {
            @Override
            public void onSuccess() {
                showToast(R.string.vehicle_added_success);
            }

            @Override
            public void onFailure() {
                showToast(R.string.vehicle_added_failure);
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void showToast(int resId) {
        Toast.makeText(requireContext(), getString(resId), Toast.LENGTH_SHORT).show();
    }
}
