package com.lksnext.parkingplantilla.domain;


public class ParkingItem {

    private  int imageID;
    private String address;

    private Reserva reserva;

    public ParkingItem() {
    }
    public ParkingItem(int imageID, String address, Reserva reserva) {
        this.imageID = imageID;
        this.address = address;

        this.reserva = reserva;
    }
    public int getImageID() {
        return imageID;
    }
    public void setImageID(int imageID) {
        this.imageID = imageID;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }


    public Reserva getReserva() {
        return reserva;
    }
    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
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
        if (!(o instanceof ParkingItem)) return false;

        ParkingItem that = (ParkingItem) o;

        if (imageID != that.imageID) return false;
        if (!address.equals(that.address)) return false;
        return reserva != null ? reserva.equals(that.reserva) : that.reserva == null;
    }


}
