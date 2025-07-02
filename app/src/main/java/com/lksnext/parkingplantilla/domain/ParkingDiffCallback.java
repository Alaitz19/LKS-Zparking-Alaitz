package com.lksnext.parkingplantilla.domain;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

public class ParkingDiffCallback extends DiffUtil.Callback {
    private final List<ParkingItem> oldList;
    private final List<ParkingItem> newList;

    public ParkingDiffCallback(List<ParkingItem> oldList, List<ParkingItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Reserva oldReserva = oldList.get(oldItemPosition).getReserva();
        Reserva newReserva = newList.get(newItemPosition).getReserva();
        return oldReserva != null && newReserva != null &&
                Objects.equals(oldReserva.getId(), newReserva.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}

