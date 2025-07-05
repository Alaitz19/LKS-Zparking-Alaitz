package com.lksnext.parkingplantilla.domain;

public class Vehicle {

    private String matricula;
    private String marca;
    private String pollutionType;
    private String imageUrl;


    public String getMatricula() {
        return matricula;
    }
    public void setId(String id) {
        this.matricula = id;
    }

    public String getMarca() {
        return marca;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLez() {
        if (pollutionType == null) {
            return "Unknown";
        }
        return switch (pollutionType) {
            case "B" -> "B";
            case "C" -> "C";
            case "ECO" -> "ECO";
            case "ZERO" -> "ZERO";
            default -> "Unknown";
        };
    }

}

