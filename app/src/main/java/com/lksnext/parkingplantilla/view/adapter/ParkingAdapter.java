package com.lksnext.parkingplantilla.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.ParkingDiffCallback;
import com.lksnext.parkingplantilla.domain.ParkingItem;
import com.lksnext.parkingplantilla.domain.Reserva;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {

    private Context context;
    private List<ParkingItem> parkingList;
    private OnDeleteClickListener deleteClickListener;

    public ParkingAdapter(Context context, List<ParkingItem> parkingList) {
        this.context = context;
        this.parkingList = parkingList;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
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

        if (item.getReserva() != null && item.getReserva().getPlaza() != null) {
            holder.parkingNumber.setText(item.getReserva().getPlaza().toString());
            holder.parkingType.setText("Tipo: " + item.getReserva().getPlaza().getTipoPlaza().name());
            Log.d("ParkingAdapter", "Reserva encontrada: " + item.getReserva().toString());
        } else {
            holder.parkingNumber.setText("N/A");
            holder.parkingType.setText("Tipo: N/A");
        }

        holder.parkingAddress.setText(item.getAddress());
        holder.startTimer(item);


        holder.btnEdit.setOnClickListener(v -> {
            // lógica para editar
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDelete(item, position);
            }
        });
    }
    public void removeItem(int position) {
        if (position >= 0 && position < parkingList.size()) {
            parkingList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ParkingViewHolder holder) {
        super.onViewRecycled(holder);
        holder.stopTimer();
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    public static class ParkingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageParking;
        TextView parkingNumber, parkingAddress, parkingType, parkingTime;
        ImageButton btnView, btnEdit, btnDelete;
        Handler handler = new Handler();
        Runnable updateRunnable;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageParking = itemView.findViewById(R.id.image_parking);
            parkingNumber = itemView.findViewById(R.id.parking_number);
            parkingAddress = itemView.findViewById(R.id.parking_address);
            parkingTime = itemView.findViewById(R.id.parking_time);
            parkingType = itemView.findViewById(R.id.parking_type);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void startTimer(ParkingItem item) {
            stopTimer();
            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    parkingTime.setText(item.getReservaInfo());
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(updateRunnable);
        }

        public void stopTimer() {
            if (updateRunnable != null) handler.removeCallbacks(updateRunnable);
        }
    }

    public interface OnDeleteClickListener {
        void onDelete(ParkingItem item, int position);
    }}