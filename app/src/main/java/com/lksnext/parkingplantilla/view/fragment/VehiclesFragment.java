package com.lksnext.parkingplantilla.view.fragment;

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

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.view.adapter.VehicleAdapter;
import com.lksnext.parkingplantilla.viewmodel.VehiclesViewModel;

import java.util.ArrayList;

public class VehiclesFragment extends Fragment {

    private Uri selectedImageUri;
    private ImageView dialogImageView;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public VehiclesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vehicles, container, false);

        // Configura RecyclerView + Adapter + ViewModel
        RecyclerView recyclerView = view.findViewById(R.id.vehicles_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        VehicleAdapter adapter = new VehicleAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        VehiclesViewModel viewModel = new ViewModelProvider(this).get(VehiclesViewModel.class);
        viewModel.getVehicles().observe(getViewLifecycleOwner(), adapter::setVehicles);

        // Botón ➕ para añadir vehículo
        View addVehicleButton = view.findViewById(R.id.add_vehicle_button);
        addVehicleButton.setOnClickListener(v -> showAddVehicleDialog());

        // Launcher para abrir galería
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (dialogImageView != null) {
                            dialogImageView.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        return view;
    }

    private void showAddVehicleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.dialog_add_vehicle_title));

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_vehicle, null);

        EditText editTextPlate = dialogView.findViewById(R.id.editTextPlate);
        Spinner spinnerPollution = dialogView.findViewById(R.id.spinnerPollution);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);
        dialogImageView = dialogView.findViewById(R.id.imageViewVehicle);

        dialogImageView.setOnClickListener(v -> openGallery());

        builder.setView(dialogView);

        builder.setPositiveButton(getString(R.string.dialog_add_vehicle_positive), (dialog, which) -> {
            String plate = editTextPlate.getText().toString().trim();
            String pollutionType = spinnerPollution.getSelectedItem().toString();
            String selectedType = spinnerType.getSelectedItem().toString();

            if (plate.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_plate_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri != null) {
                DataRepository.getInstance().addVehiculo(
                        plate,
                        pollutionType,
                        selectedType,
                        selectedImageUri,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(requireContext(), getString(R.string.vehicle_added_success), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(requireContext(), getString(R.string.vehicle_added_failure), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_image_required), Toast.LENGTH_SHORT).show();
            }

            selectedImageUri = null;
        });

        builder.setNegativeButton(getString(R.string.dialog_add_vehicle_negative), (dialog, which) -> {
            dialog.dismiss();
            selectedImageUri = null;
        });

        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }
}
