package com.lksnext.parkingplantilla.domain;


import androidx.annotation.NonNull;

import java.util.Objects;

public record ParkingItem(int imageID, String address, Reserva reserva) {

    @NonNull
    @Override
    public String toString() {
        return "ParkingItem{" +
                "imageID=" + imageID +
                ", address='" + address + '\'' +

                ", reserva=" + reserva +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingItem that)) return false;

        if (imageID != that.imageID) return false;
        if (!address.equals(that.address)) return false;
        return Objects.equals(reserva, that.reserva);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(imageID);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (reserva != null ? reserva.hashCode() : 0);
        return result;
    }


}
