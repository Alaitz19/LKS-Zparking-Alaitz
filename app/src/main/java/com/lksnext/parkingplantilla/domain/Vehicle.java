package com.lksnext.parkingplantilla.domain;

public class Vehicle {

    private String matricula;
    private String tipo;
    private String contaminacion;
    private String imagenUrl;

    public Vehicle() {
        // Necesario para Firestore
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getContaminacion() {
        return contaminacion;
    }

    public void setContaminacion(String contaminacion) {
        this.contaminacion = contaminacion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getLez() {
        if (contaminacion == null) {
            return "Unknown";
        }
        return switch (contaminacion) {
            case "B" -> "B";
            case "C" -> "C";
            case "ECO" -> "ECO";
            case "ZERO" -> "ZERO";
            default -> "Unknown";
        };
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
