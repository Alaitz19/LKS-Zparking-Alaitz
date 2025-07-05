package com.lksnext.parkingplantilla.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicles = new ArrayList<>();

    public VehicleAdapter(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle_card, parent, false);
        return new VehicleViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);

        holder.vehiclePlate.setText(
                holder.itemView.getContext().getString(R.string.vehicle_plate_label, vehicle.getMatricula())
        );

        holder.vehicleLez.setText(
                holder.itemView.getContext().getString(R.string.vehicle_lez_label, vehicle.getLez())
        );

        holder.vehicletype.setText(
                holder.itemView.getContext().getString(R.string.vehicle_type_label, vehicle.getMarca())
        );

        Glide.with(holder.vehicleImage.getContext())
                .load(vehicle.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(holder.vehicleImage);
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
        notifyDataSetChanged();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {

        ImageView vehicleImage;
        TextView vehicletype, vehiclePlate, vehicleLez;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            vehicleImage = itemView.findViewById(R.id.vehicle_image);
            vehiclePlate = itemView.findViewById(R.id.vehicle_plate);
            vehicleLez = itemView.findViewById(R.id.vehicle_lez);
            vehicletype = itemView.findViewById(R.id.vehicle_type);
        }
    }
}
