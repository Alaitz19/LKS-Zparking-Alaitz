package com.lksnext.parkingplantilla.view.adapter;

import android.content.Context;
import android.os.Handler;
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
import com.lksnext.parkingplantilla.domain.Reserva;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {

    private final Context context;
    private final List<ParkingItem> parkingList;

    private OnDeleteClickListener deleteClickListener;
    private OnEditClickListener editClickListener;

    public ParkingAdapter(Context context, List<ParkingItem> parkingList) {
        this.context = context;
        this.parkingList = parkingList;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
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
        Reserva reserva = item.reserva();

        holder.imageParking.setImageResource(item.imageID());
        holder.parkingAddress.setText(item.address());

        if (reserva != null) {
            holder.parkingNumber.setText(reserva.getPlaza().getCodigo());
            holder.parkingType.setText(
                    context.getString(R.string.parking_type_label, reserva.getPlaza().getTipoPlaza().name())
            );
        } else {
            holder.parkingNumber.setText(R.string.parking_na);
            holder.parkingType.setText(
                    context.getString(R.string.parking_type_label, context.getString(R.string.parking_na))
            );
        }

        setupTimeRange(holder, reserva);
        setupDate(holder, reserva);

        holder.startTimer(item);

        setupButtons(holder, item, reserva, position);
    }

    private void setupTimeRange(ParkingViewHolder holder, Reserva reserva) {
        if (reserva != null) {
            List<String> horas = reserva.getHora().getHoras();
            if (!horas.isEmpty()) {
                String inicio = horas.get(0);
                String fin = horas.get(horas.size() - 1);
                holder.parkingTimeRange.setText(
                        context.getString(R.string.parking_time_range, inicio, fin)
                );
                return;
            }
        }
        holder.parkingTimeRange.setText(R.string.parking_no_hours);
    }

    private void setupDate(ParkingViewHolder holder, Reserva reserva) {
        if (reserva != null && reserva.getFecha() != null) {
            String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(reserva.getFecha().toDate());
            holder.parkingTime.setText(
                    context.getString(R.string.parking_date, fechaFormateada)
            );
        } else {
            holder.parkingTime.setText(
                    context.getString(R.string.parking_date, context.getString(R.string.parking_na))
            );
        }
    }

    private void setupButtons(ParkingViewHolder holder, ParkingItem item, Reserva reserva, int position) {
        if (puedeEditar(reserva)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (editClickListener != null) editClickListener.onEdit(item);
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
        }

        if (puedeBorrar(reserva)) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteClickListener != null) deleteClickListener.onDelete(item, position);
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    private boolean puedeEditar(Reserva reserva) {
        return esReservaValidaYActiva(reserva);
    }

    private boolean puedeBorrar(Reserva reserva) {
        return esReservaValidaYActiva(reserva);
    }

    private boolean esReservaValidaYActiva(Reserva reserva) {
        if (reserva == null || reserva.getFecha() == null) return false;
        try {
            String horaFinStr = reserva.getHora().getHoraFin();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(reserva.getFecha().toDate());
            long now = System.currentTimeMillis();
            long millisFin = Objects.requireNonNull(sdf.parse(fechaStr + " " + horaFinStr)).getTime();
            return millisFin >= now;
        } catch (Exception e) {
            return false;
        }
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

        final ImageView imageParking;
        final TextView parkingNumber;
        final TextView parkingType;
        final TextView parkingTime;
        final TextView parkingTimeRange;
        final TextView parkingAddress;
        final ImageButton  btnDelete;
        final ImageButton btnEdit;

        private final Handler handler = new Handler();
        private Runnable updateRunnable;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageParking = itemView.findViewById(R.id.image_parking);
            parkingNumber = itemView.findViewById(R.id.parking_number);
            parkingAddress = itemView.findViewById(R.id.parking_address);
            parkingTime = itemView.findViewById(R.id.parking_time);
            parkingTimeRange = itemView.findViewById(R.id.parking_time_range);
            parkingType = itemView.findViewById(R.id.parking_type);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void startTimer(ParkingItem item) {
            stopTimer();
            updateRunnable = new Runnable() {
                @Override
                public void run() {
                    updateTimer(item);
                    handler.postDelayed(this, 1000);
                }
            };
            handler.post(updateRunnable);
        }

        private void updateTimer(ParkingItem item) {
            Reserva reserva = item.reserva();
            Context context = itemView.getContext();
            if (reserva != null && reserva.getFecha() != null) {
                try {
                    List<String> horas = reserva.getHora().getHoras();
                    if (!horas.isEmpty()) {
                        String horaInicio = horas.get(0);
                        String horaFin = horas.get(horas.size() - 1);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        String fechaStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                .format(reserva.getFecha().toDate());

                        long now = System.currentTimeMillis();
                        long millisInicio = Objects.requireNonNull(sdf.parse(fechaStr + " " + horaInicio)).getTime();
                        long millisFin = Objects.requireNonNull(sdf.parse(fechaStr + " " + horaFin)).getTime();

                        if (now < millisInicio) {
                            parkingTime.setText(context.getString(R.string.parking_starts_in, formatMillis(millisInicio - now)));
                        } else if (now < millisFin) {
                            parkingTime.setText(context.getString(R.string.parking_time_remaining, formatMillis(millisFin - now)));
                        } else {
                            parkingTime.setText(context.getString(R.string.parking_date, fechaStr));
                        }
                    } else {
                        parkingTime.setText(R.string.parking_no_hours);
                    }
                } catch (Exception e) {
                    parkingTime.setText(R.string.parking_time_error);
                }
            } else {
                parkingTime.setText(R.string.parking_no_reservation);
            }
        }

        private String formatMillis(long millis) {
            long seconds = millis / 1000;
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
        }

        public void stopTimer() {
            if (updateRunnable != null) {
                handler.removeCallbacks(updateRunnable);
                updateRunnable = null;
            }
        }
    }

    public interface OnDeleteClickListener {
        void onDelete(ParkingItem item, int position);
    }

    public interface OnEditClickListener {
        void onEdit(ParkingItem item);
    }
}
