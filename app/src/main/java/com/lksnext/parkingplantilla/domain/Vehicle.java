package com.lksnext.parkingplantilla.domain;

public class Vehicle {

    private String matricula;
    private String marca;
    private String pollutionType;
    private boolean isElectric;
    private String imageUrl;


    public Vehicle() {}

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPollutionType() {
        return pollutionType;
    }

    public void setPollutionType(String pollutionType) {
        this.pollutionType = pollutionType;
    }

    public boolean isElectric() {
        return isElectric;
    }

    public void setElectric(boolean electric) {
        isElectric = electric;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setId(String id) {
    }

    public String getLez() {
        if (pollutionType == null) {
            return "Unknown";
        }
        switch (pollutionType) {
            case "B":
                return "B";
            case "C":
                return "C";
            case "ECO":
                return "ECO";
            case "ZERO":
                return "ZERO";
            default:
                return "Unknown";
        }
    }
}
