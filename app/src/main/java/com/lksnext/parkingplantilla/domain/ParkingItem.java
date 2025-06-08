package com.lksnext.parkingplantilla.domain;

public class ParkingItem {

    private  int imageID;
    private String address;
    private Plaza plaza;
    private Reserva reserva;

    public ParkingItem() {
    }
    public ParkingItem(int imageID, String address, Plaza plaza, Reserva reserva) {
        this.imageID = imageID;
        this.address = address;
        this.plaza = plaza;
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
    public Plaza getPlaza() {
        return plaza;
    }
    public void setPlaza(Plaza plaza) {
        this.plaza = plaza;
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
                ", plaza=" + plaza +
                ", reserva=" + reserva +
                '}';
    }


}
