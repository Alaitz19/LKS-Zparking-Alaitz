package com.lksnext.parkingplantilla.view.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.ParkingItem;

import java.util.List;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {

    private Context context;
    private List<ParkingItem> parkingList;

    public ParkingAdapter(Context context, List<ParkingItem> parkingList) {
        this.context = context;
        this.parkingList = parkingList;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parking, parent, false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        ParkingItem item = parkingList.get(position);
        holder.imageParking.setImageResource(item.getImageID());

        // Si tienes plaza con número
        if (item.getPlaza() != null) {
            holder.parkingNumber.setText(String.valueOf(item.getPlaza().getNumero()));
        } else {
            holder.parkingNumber.setText("N/A");
        }

        holder.parkingAddress.setText(item.getAddress());

        holder.parkingTime.setText(item.getReservaInfo());


        // Acciones en los botones
        holder.btnView.setOnClickListener(v -> {
            // Lógica de ver
        });

        holder.btnEdit.setOnClickListener(v -> {
            // Lógica de editar
        });

        holder.btnDelete.setOnClickListener(v -> {
            // Lógica de eliminar
        });
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    public static class ParkingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageParking;
        TextView parkingNumber, parkingAddress, parkingTime;
        ImageButton btnView, btnEdit, btnDelete;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageParking = itemView.findViewById(R.id.image_parking);
            parkingNumber = itemView.findViewById(R.id.parking_number);
            parkingAddress = itemView.findViewById(R.id.parking_address);
            parkingTime = itemView.findViewById(R.id.parking_time);
            btnView = itemView.findViewById(R.id.btn_view);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}

